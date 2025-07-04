package com.tu.servidor.vigilante;

import com.tu.servidor.vigilante.modules.CheckManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VigilanteAntiCheat extends JavaPlugin {

    private CheckManager checkManager;

    @Override
    public void onEnable() {
        // Iniciar y registrar todos nuestros módulos de detección
        this.checkManager = new CheckManager(this);
        checkManager.registerChecks();

        getLogger().info("VigilanteAntiCheat v0.2 ha sido activado con arquitectura modular.");
    }

    @Override
    public void onDisable() {
        getLogger().info("VigilanteAntiCheat ha sido desactivado.");
    }
}