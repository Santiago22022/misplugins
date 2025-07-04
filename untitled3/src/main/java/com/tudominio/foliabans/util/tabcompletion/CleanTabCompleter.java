package com.tudominio.foliabans.util.tabcompletion;

import com.tudominio.foliabans.FoliaBans;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CleanTabCompleter implements MutableTabCompleter {
    private final MutableTabCompleter rawTabCompleter;
    public static final String PLAYER_PLACEHOLDER = "PLAYERS";

    public CleanTabCompleter(MutableTabCompleter rawTabCompleter) {
        this.rawTabCompleter = rawTabCompleter;
    }

    @Override
    public ArrayList<String> onTabComplete(Object user, String[] args) {
        ArrayList<String> suggestions = rawTabCompleter.onTabComplete(user, args);

        if(!suggestions.isEmpty() && suggestions.get(0).equals(PLAYER_PLACEHOLDER)) {
            suggestions.remove(0);
            for (Player player : Bukkit.getOnlinePlayers()){
                suggestions.add(player.getName());
            }
        }

        if(args.length > 0)
            suggestions.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));

        return suggestions;
    }
}