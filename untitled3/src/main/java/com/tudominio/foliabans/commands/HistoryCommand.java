package com.tudominio.foliabans.commands;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.commands.base.BaseCommand;
import com.tudominio.foliabans.db.Punishment;
import com.tudominio.foliabans.util.tabcompletion.CleanTabCompleter;
import com.tudominio.foliabans.util.tabcompletion.MutableTabCompleter;
import com.tudominio.foliabans.util.tabcompletion.PunishmentTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryCommand extends BaseCommand {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public HistoryCommand(FoliaBans plugin) {
        super(plugin, new CleanTabCompleter((MutableTabCompleter) new PunishmentTabCompleter(false)));
        this.setName("history");
        this.setDescription("Muestra el historial de castigos de un jugador");
        this.setUsage("/history <jugador>");
        this.setPermission("foliabans.history");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!hasPermission(sender, "foliabans.history")) {
            sendMessage(sender, "General.NoPerms");
            return true;
        }

        if (args.length < 1) {
            sendMessage(sender, "History.Usage");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Obtener historial
        plugin.getStorageManager().getBackend().getHistory(target.getUniqueId())
                .thenAccept(history -> {
                    plugin.getSchedulerAdapter().runTask(() -> {
                        displayHistory(sender, targetName, history);
                    });
                })
                .exceptionally(throwable -> {
                    plugin.getSchedulerAdapter().runTask(() -> {
                        sendMessage(sender, "General.DatabaseError");
                    });
                    return null;
                });

        return true;
    }

    private void displayHistory(CommandSender sender, String playerName, List<Punishment> history) {
        if (history.isEmpty()) {
            sendMessage(sender, "History.NoHistory", "PLAYER", playerName);
            return;
        }

        sendMessage(sender, "History.Header", "PLAYER", playerName);

        for (Punishment punishment : history) {
            String formattedDate = DATE_FORMAT.format(new Date(punishment.time()));
            String message = plugin.getLangManager().getMessage("History.Entry")
                    .replace("%DATE%", formattedDate)
                    .replace("%TYPE%", punishment.type().getName())
                    .replace("%REASON%", punishment.reason())
                    .replace("%OPERATOR%", punishment.banner());

            sender.sendMessage(message);
        }
    }
}