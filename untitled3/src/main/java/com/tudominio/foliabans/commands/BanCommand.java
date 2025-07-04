package com.tudominio.foliabans.commands;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.commands.base.BaseCommand;
import com.tudominio.foliabans.db.Punishment;
import com.tudominio.foliabans.db.PunishmentType;
import com.tudominio.foliabans.events.PunishmentEvent;
import com.tudominio.foliabans.util.TimeManager;
import com.tudominio.foliabans.util.tabcompletion.CleanTabCompleter;
import com.tudominio.foliabans.util.tabcompletion.MutableTabCompleter;
import com.tudominio.foliabans.util.tabcompletion.PunishmentTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BanCommand extends BaseCommand {

    public BanCommand(FoliaBans plugin) {
        super(plugin, new CleanTabCompleter((MutableTabCompleter) new PunishmentTabCompleter(false)));
        this.setName("ban");
        this.setDescription("Banea permanentemente a un jugador");
        this.setUsage("/ban <jugador> [razón]");
        this.setPermission("foliabans.ban");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!hasPermission(sender, "foliabans.ban")) {
            sendMessage(sender, "General.NoPerms");
            return true;
        }

        if (args.length < 1) {
            sendMessage(sender, "Ban.Usage");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Verificar si el jugador está exento
        if (target.isOnline() && target.getPlayer().hasPermission("foliabans.exempt")) {
            sendMessage(sender, "Ban.Exempt", "PLAYER", targetName);
            return true;
        }

        // Verificar si ya está baneado
        plugin.getStorageManager().getBackend().getActiveBan(target.getUniqueId())
                .thenAccept(banInfo -> {
                    if (banInfo != null) {
                        plugin.getSchedulerAdapter().runTask(() ->
                                sendMessage(sender, "Ban.AlreadyBanned", "PLAYER", targetName));
                        return;
                    }

                    // Procesar el ban
                    String reason = args.length > 1 ?
                            String.join(" ", Arrays.copyOfRange(args, 1, args.length)) :
                            plugin.getConfig().getString("settings.default-reason", "Violación de las reglas");

                    String bannerName = sender instanceof Player ? sender.getName() : "CONSOLE";
                    long until = 0; // Ban permanente

                    // Añadir ban
                    plugin.getStorageManager().getBackend().addBan(target.getUniqueId(), reason, bannerName, until);

                    // Crear punishment para historial
                    Punishment punishment = new Punishment(
                            target.getUniqueId(),
                            Punishment.Type.BAN,
                            reason,
                            bannerName,
                            TimeManager.getTime()
                    );
                    plugin.getStorageManager().getBackend().logPunishment(punishment);

                    // Disparar evento
                    plugin.getSchedulerAdapter().runTask(() -> {
                        PunishmentEvent event = new PunishmentEvent(punishment, false);
                        Bukkit.getPluginManager().callEvent(event);
                    });

                    // Expulsar al jugador si está conectado
                    if (target.isOnline()) {
                        plugin.getSchedulerAdapter().runTaskForPlayer(target.getPlayer(), () -> {
                            target.getPlayer().kickPlayer(
                                    plugin.getLangManager().getMessage("Ban.Layout")
                                            .replace("%REASON%", reason)
                                            .replace("%OPERATOR%", bannerName)
                                            .replace("%DURATION%", "Permanente")
                            );
                        });
                    }

                    // Enviar confirmación
                    plugin.getSchedulerAdapter().runTask(() -> {
                        sendMessage(sender, "Ban.Success",
                                "PLAYER", targetName,
                                "REASON", reason);

                        // Notificar a staff
                        plugin.getServer().getOnlinePlayers().stream()
                                .filter(p -> p.hasPermission("foliabans.notify"))
                                .forEach(p -> p.sendMessage(
                                        plugin.getLangManager().getMessage("Ban.Notification")
                                                .replace("%PLAYER%", targetName)
                                                .replace("%OPERATOR%", bannerName)
                                                .replace("%REASON%", reason)
                                ));
                    });
                });

        return true;
    }
}