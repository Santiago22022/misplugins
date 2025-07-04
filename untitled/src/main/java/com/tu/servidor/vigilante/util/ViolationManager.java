package com.tu.servidor.vigilante.util;

import com.tu.servidor.vigilante.modules.ICheck;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViolationManager {

    private static final Map<UUID, Map<String, Integer>> violationLevels = new HashMap<>();
    private static final int VIOLATION_THRESHOLD = 15;

    public static void addViolation(Player player, ICheck check, String debugInfo) {
        violationLevels.putIfAbsent(player.getUniqueId(), new HashMap<>());
        Map<String, Integer> playerViolations = violationLevels.get(player.getUniqueId());

        int vl = playerViolations.getOrDefault(check.getCheckName(), 0) + 1;
        playerViolations.put(check.getCheckName(), vl);

        if (vl > VIOLATION_THRESHOLD) {
            notifyStaff(player, check, vl, debugInfo);
            playerViolations.remove(check.getCheckName()); // Reiniciamos para no spamear
        }
    }

    private static void notifyStaff(Player cheater, ICheck check, int vl, String debugInfo) {
        String message = ChatColor.RED + "[Vigilante] " +
                ChatColor.YELLOW + cheater.getName() +
                ChatColor.GRAY + " ha activado la deteccion " +
                ChatColor.WHITE + check.getCheckName() + ". " +
                ChatColor.DARK_GRAY + "(" + debugInfo + " | VL: " + vl + ")";

        Bukkit.getLogger().info(message);
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("vigilante.notify")) {
                staff.sendMessage(message);
            }
        }
    }
}