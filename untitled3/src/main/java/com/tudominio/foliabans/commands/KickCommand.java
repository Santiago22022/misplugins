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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class KickCommand extends BaseCommand {

    public KickCommand(FoliaBans plugin) {
        super(plugin, new CleanTabCompleter((MutableTabCompleter) new PunishmentTabCompleter(false)));
        this.setName("kick");
        this.setDescription("Expulsa a un jugador del servidor");
        this.setUsage("/kick <jugador> [razón]");
        this.setPermission("foliabans.kick");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!hasPermission(sender, "foliabans.kick")) {
            sendMessage(sender, "General.NoPerms");
            return true;
        }

        if (args.length < 1) {
            sendMessage(sender, "Kick.Usage");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sendMessage(sender, "General.PlayerOffline", "PLAYER", targetName);
            return true;
        }

        // Verificar si el jugador está exento
        if (target.hasPermission("foliabans.exempt")) {
            sendMessage(sender, "Ban.Exempt", "PLAYER", targetName);
            return true;
        }

        String reason = args.length > 1 ?
                String.join(" ", Arrays.copyOfRange(args, 1, args.length)) :
                plugin.getConfig().getString("settings.default-reason", "Violación de las reglas");

        String operatorName = sender instanceof Player ? sender.getName() : "CONSOLE";

        // Crear punishment para historial
        Punishment punishment = new Punishment(
                target.getUniqueId(),
                Punishment.Type.KICK,
                reason,
                operatorName,
                TimeManager.getTime()
        );
        plugin.getStorageManager().getBackend().logPunishment(punishment);

        // Disparar evento
        PunishmentEvent event = new PunishmentEvent(punishment, false);
        Bukkit.getPluginManager().callEvent(event);

        // Expulsar al jugador
        plugin.getSchedulerAdapter().runTaskForPlayer(target, () -> {
            target.kickPlayer(
                    plugin.getLangManager().getMessage("Kick.Message")
                            .replace("%REASON%", reason)
            );
        });

        sendMessage(sender, "Kick.Success", "PLAYER", targetName);

        return true;
    }
}