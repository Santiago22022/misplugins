package com.tudominio.foliabans.util.tabcompletion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PunishmentTabCompleter implements TabCompleter {

    private final boolean temporary;

    public PunishmentTabCompleter(boolean temporary) {
        this.temporary = temporary;
    }

    @Override
    public List<String> onTabComplete(Object user, String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            // Primer argumento: jugador
            suggestions.add(CleanTabCompleter.PLAYER_PLACEHOLDER);
        } else if (args.length == 2 && temporary) {
            // Segundo argumento para comandos temporales: tiempo
            suggestions.add("1h");
            suggestions.add("1d");
            suggestions.add("1w");
            suggestions.add("30d");
        } else if ((args.length == 2 && !temporary) || (args.length == 3 && temporary)) {
            // Razón del castigo
            suggestions.add("Griefing");
            suggestions.add("Hacks");
            suggestions.add("Spam");
            suggestions.add("Comportamiento tóxico");
        }
        
        return suggestions;
    }
}