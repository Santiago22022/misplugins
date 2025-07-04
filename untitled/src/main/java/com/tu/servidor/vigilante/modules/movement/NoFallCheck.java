package com.tu.servidor.vigilante.modules.movement;

import com.tu.servidor.vigilante.modules.ICheck;
import com.tu.servidor.vigilante.util.ViolationManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoFallCheck implements ICheck {

    @Override
    public String getCheckName() {
        return "NoFall";
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Ignoramos a jugadores que no pueden recibir daño de caída
        if (player.getAllowFlight() || player.getGameMode() == GameMode.CREATIVE || player.isGliding()) {
            return;
        }

        // El servidor nos dice si el paquete que acaba de llegar del cliente dice "onGround = true"
        boolean clientClaimsToBeOnGround = player.isOnGround();

        // El servidor lleva su propio registro de la distancia de caída
        float serverFallDistance = player.getFallDistance();

        // LA DETECCIÓN CLAVE:
        // Si el cliente dice que está en el suelo, PERO el servidor sabe que ha caído
        // una distancia suficiente como para hacerse daño (> 3 bloques), es una mentira.
        // Un jugador legítimo recibiría el daño y su fallDistance se resetearía a 0.
        if (clientClaimsToBeOnGround && serverFallDistance > 3.0) {
            ViolationManager.addViolation(player, this, "Reseteo de daño de caída anómalo. Dist: " + String.format("%.2f", serverFallDistance));
        }
    }
}