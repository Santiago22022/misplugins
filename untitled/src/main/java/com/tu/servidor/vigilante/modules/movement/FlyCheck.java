package com.tu.servidor.vigilante.modules.movement;

import com.tu.servidor.vigilante.modules.ICheck;
import com.tu.servidor.vigilante.util.ViolationManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class FlyCheck implements ICheck {

    // MEJORA: Lista de materiales resbaladizos como el hielo.
    private static final List<Material> ICE_MATERIALS = Arrays.asList(
            Material.ICE,
            Material.PACKED_ICE,
            Material.BLUE_ICE,
            Material.FROSTED_ICE
    );

    @Override
    public String getCheckName() {
        return "Fly/Speed";
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.hasPermission("vigilante.bypass") || player.getAllowFlight() || player.isInsideVehicle() || player.isGliding() || player.isSwimming()) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        // Solo continuamos si hubo un movimiento real
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        checkHorizontalSpeed(player, from, to);
        checkGravity(player, from, to);
    }

    private void checkHorizontalSpeed(Player player, Location from, Location to) {
        double horizontalDistance = Math.sqrt(Math.pow(to.getX() - from.getX(), 2) + Math.pow(to.getZ() - from.getZ(), 2));
        if (horizontalDistance == 0) return;

        // --- CÁLCULO DE VELOCIDAD MEJORADO ---
        double maxSpeed;

        // MEJORA: Umbrales base ligeramente más generosos para evitar falsos positivos por picos de lag.
        if (player.isSprinting()) {
            maxSpeed = 0.50; // Antes 0.45
        } else {
            maxSpeed = 0.38; // Antes 0.35
        }

        // Se mantiene el cálculo por efectos de poción
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            int speedLevel = player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 1;
            maxSpeed += speedLevel * 0.20; // Un poco más de margen por nivel
        }

        // MEJORA: Detección de Hielo
        Material groundMaterial = player.getLocation().getBlock().getRelative(0, -1, 0).getType();
        if (ICE_MATERIALS.contains(groundMaterial)) {
            maxSpeed += 0.40; // Aumentamos mucho el límite si está sobre hielo
        }

        // MEJORA: Lógica de salto más precisa. El extra de velocidad solo se aplica si está realmente ascendiendo.
        if (to.getY() > from.getY() && !player.isOnGround()) {
            maxSpeed += 0.25;
        }

        if (horizontalDistance > maxSpeed) {
            ViolationManager.addViolation(player, this, "Velocidad horizontal anómala. Vel: " + String.format("%.2f", horizontalDistance) + ", Max: " + String.format("%.2f", maxSpeed));
        }
    }

    private void checkGravity(Player player, Location from, Location to) {
        if (isNearGround(player)) return;

        double verticalDistance = to.getY() - from.getY();

        if (verticalDistance > 0.42) {
            ViolationManager.addViolation(player, this, "Ascenso vertical anómalo. VelY: " + String.format("%.2f", verticalDistance));
        }

        boolean isFalling = verticalDistance < -0.08;
        boolean isJumping = verticalDistance > 0;

        if (!isFalling && !isJumping) {
            ViolationManager.addViolation(player, this, "Gravedad inconsistente (flotando). VelY: " + String.format("%.2f", verticalDistance));
        }
    }

    private boolean isNearGround(Player player) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location checkLoc = player.getLocation().add(x, -1, z);
                if (checkLoc.getBlock().getType().isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }
}