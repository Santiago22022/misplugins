package com.tu.servidor.vigilante.modules;

import com.tu.servidor.vigilante.VigilanteAntiCheat;
import com.tu.servidor.vigilante.modules.combat.KillAuraCheck;
import com.tu.servidor.vigilante.modules.movement.FlyCheck;
import com.tu.servidor.vigilante.modules.movement.NoFallCheck; // <-- IMPORTA LA NUEVA CLASE
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {

    final VigilanteAntiCheat plugin;
    private final List<ICheck> checks = new ArrayList<>();

    public CheckManager(VigilanteAntiCheat plugin) {
        this.plugin = plugin;
    }

    public void registerChecks() {
        // Aquí añadimos todas las detecciones que queremos activar
        boolean add= checks.add(new FlyCheck());
        boolean  add = checks.add(new KillAuraCheck());
        boolean add = checks.add(new NoFallCheck());

        // Registrar cada check como un listener para que reciba eventos
        for (ICheck check : checks) {
            Bukkit.getPluginManager().registerEvents(check, plugin);
            plugin.getLogger().info("Módulo de detección activado: " + check.getCheckName());
        }
    }
}