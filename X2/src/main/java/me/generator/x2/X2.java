package me.generator.x2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class X2 extends JavaPlugin {
    public static String prefix = "&7[&cx2&7]";
    private String version = getDescription().getVersion();
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+" Se ha activado Version: "+version));
    }public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+" Se ha desactivado Version: "+version));
    }
    public void RegisterCommands() {
        this.getCommand("miprimerplugin").setExecutor(comandoa);
    }
}
