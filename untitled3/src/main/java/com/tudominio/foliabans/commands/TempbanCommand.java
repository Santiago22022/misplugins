package com.tudominio.foliabans.commands;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.commands.base.BaseCommand;
import com.tudominio.foliabans.db.Punishment;
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

public class TempbanCommand extends BaseCommand {

    public TempbanCommand(FoliaBans plugin) {
        super(plugin, new CleanTabCompleter((MutableTabCompleter) new PunishmentTabCompleter(true)));
        this.setName("tempban");
        this.setDescription("Banea temporalmente a un jugador");
        this.setUsage("/tempban <jugador> <tiempo> [razón]");
        this.setPermission("foliabans.tempban");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!hasPermission(sender, "foliabans.tempban")) {
            sendMessage(sender, "General.NoPerms");
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, "TempBan.Usage");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Verificar si el jugador está exento
        if (target.isOnline() && target.getPlayer().hasPermission("foliabans.exempt")) {
            sendMessage(sender, "Ban.Exempt", "PLAYER", targetName);
            return true;
        }

        // Parsear duración
        long duration = TimeManager.toMilliSec(args[1]);
        if (duration <= 0) {
            sendMessage(sender, "General.InvalidTime", "TIME", args[1]);
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

                    // Procesar el tempban
                    String reason = args.length > 2 ?
                            String.join(" ", Arrays.copyOfRange(args, 2, args.length)) :
                            plugin.getConfig().getString("settings.default-reason", "Violación de las reglas");

                    String bannerName = sender instanceof Player ? sender.getName() : "CONSOLE";
                    long until = TimeManager.getTime() + duration;

                    // Añadir tempban
                    plugin.getStorageManager().getBackend().addBan(target.getUniqueId(), reason, bannerName, until);

                    // Crear punishment para historial
                    Punishment punishment = new Punishment(
                            target.getUniqueId(),
                            Punishment.Type.TEMPBAN,
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
                                            .replace("%DURATION%", TimeManager.formatTime(duration))
                            );
                        });
                    }

                    // Enviar confirmación
                    plugin.getSchedulerAdapter().runTask(() -> {
                        sendMessage(sender, "TempBan.Success",
                                "PLAYER", targetName,
                                "DURATION", TimeManager.formatTime(duration));
                    });
                });

        return true;
    }
}