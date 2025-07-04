// Archivo: UnbanCommand.java (ACTUALIZADO)
// Ubicación: src/main/java/com/tudominio/foliabans/commands/UnbanCommand.java
package com.tudominio.foliabans.commands;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.commands.base.BaseCommand;
import com.tudominio.foliabans.db.Punishment;
import com.tudominio.foliabans.events.RevokePunishmentEvent;
import com.tudominio.foliabans.util.TimeManager;
import com.tudominio.foliabans.util.tabcompletion.CleanTabCompleter;
import com.tudominio.foliabans.util.tabcompletion.PunishmentTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnbanCommand extends BaseCommand {

    public UnbanCommand(FoliaBans plugin) {
        super(plugin, new CleanTabCompleter(new PunishmentTabCompleter(false)));
        this.setName("unban");
        this.setDescription("Desbanea a un jugador");
        this.setUsage("/unban <jugador>");
        this.setPermission("foliabans.unban");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!hasPermission(sender, "foliabans.unban")) {
            sendMessage(sender, "General.NoPerms");
            return true;
        }

        if (args.length < 1) {
            sendMessage(sender, "Unban.Usage");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Verificar si está baneado y remover ban
        plugin.getStorageManager().getBackend().removeBan(target.getUniqueId())
                .thenAccept(wasRemoved -> {
                    plugin.getSchedulerAdapter().runTask(() -> {
                        if (wasRemoved) {
                            // Crear punishment para historial (UNBAN)
                            String operatorName = sender instanceof Player ? sender.getName() : "CONSOLE";
                            Punishment punishment = new Punishment(
                                    target.getUniqueId(),
                                    Punishment.Type.UNBAN,
                                    "Desbaneado",
                                    operatorName,
                                    TimeManager.getTime()
                            );
                            plugin.getStorageManager().getBackend().logPunishment(punishment);

                            // Disparar evento
                            RevokePunishmentEvent event = new RevokePunishmentEvent(punishment, false);
                            Bukkit.getPluginManager().callEvent(event);

                            sendMessage(sender, "Unban.Success", "PLAYER", targetName);
                        } else {
                            sendMessage(sender, "Unban.NotBanned", "PLAYER", targetName);
                        }
                    });
                });

        return true;
    }
}