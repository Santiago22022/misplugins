package com.tudominio.foliabans.core;

import com.tudominio.foliabans.core.hologram.HologramManager;
import com.tudominio.foliabans.core.listeners.BanListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaBansCore extends JavaPlugin {

    private HologramManager hologramManager;

    @Override
    public void onEnable() {
        // Comprobar si FoliaBans está presente
        if (getServer().getPluginManager().getPlugin("FoliaBans") == null) {
            getLogger().severe("FoliaBans no encontrado! El Core se desactivará.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.hologramManager = new HologramManager(this);
        getServer().getPluginManager().registerEvents(new BanListener(hologramManager), this);
        getLogger().info("FoliaBans-Core habilitado: ¡Funciones avanzadas activas!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FoliaBans-Core deshabilitado.");
    }
}