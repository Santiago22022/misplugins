package com.tu.servidor.vigilante.modules.combat;

import com.tu.servidor.vigilante.modules.ICheck;
import com.tu.servidor.vigilante.util.ViolationManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent; // <-- IMPORTANTE: Añadir este import
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraCheck implements ICheck {

    private final Map<UUID, Long> lastHitTime = new HashMap<>();

    @Override
    public String getCheckName() {
        return "KillAura";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        if (damager.hasPermission("vigilante.bypass")) return;

        // --- Check de Capa 1: Angle Check (Sigue igual) ---
        checkAngle(damager, event.getEntity());

        // --- Check de Capa 2: Hit Speed Check ---

        // ¡LA CORRECCIÓN!
        // Solo ejecutamos el check de velocidad si la causa del daño NO es
        // un golpe de área (sweeping edge).
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            checkHitSpeed(damager);
        }
    }

    private void checkAngle(Player damager, Entity victim) {
        Vector playerDirection = damager.getEyeLocation().getDirection();
        Vector toVictimDirection = victim.getLocation().toVector().subtract(damager.getEyeLocation().toVector()).normalize();
        double angleInDegrees = Math.toDegrees(playerDirection.angle(toVictimDirection));

        if (angleInDegrees > 95.0) {
            ViolationManager.addViolation(damager, this, "(Tipo A) Angulo de ataque invalido: " + String.format("%.2f", angleInDegrees) + "°");
        }
    }

    private void checkHitSpeed(Player damager) {
        long currentTime = System.currentTimeMillis();
        long lastHit = lastHitTime.getOrDefault(damager.getUniqueId(), 0L);
        long timeDifference = currentTime - lastHit;

        if (timeDifference < 50) {
            ViolationManager.addViolation(damager, this, "(Tipo B) Frecuencia de ataque inhumana: " + timeDifference + "ms");
        }
        lastHitTime.put(damager.getUniqueId(), currentTime);
    }
}