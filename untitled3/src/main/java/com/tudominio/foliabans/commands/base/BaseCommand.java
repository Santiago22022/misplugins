
package com.tudominio.foliabans.commands.base;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.util.tabcompletion.TabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseCommand extends Command implements CommandExecutor, TabExecutor {
    protected final FoliaBans plugin;
    private final TabCompleter tabCompleter;

    public BaseCommand(FoliaBans plugin, TabCompleter tabCompleter) {
        super("basecommand");
        this.plugin = plugin;
        this.tabCompleter = tabCompleter;
    }

    @Override
    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (tabCompleter != null) {
            return tabCompleter.onTabComplete(sender, args);
        }
        return null;
    }

    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    protected void sendMessage(CommandSender sender, String messageKey, Object... args) {
        String message = plugin.getLangManager().getMessage(messageKey);

        // Reemplazar parámetros
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                String placeholder = "%" + args[i] + "%";
                String value = String.valueOf(args[i + 1]);
                message = message.replace(placeholder, value);
            }
        }

        sender.sendMessage(message);
    }
}