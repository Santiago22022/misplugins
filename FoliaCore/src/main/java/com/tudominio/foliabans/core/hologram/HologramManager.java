package com.tudominio.foliabans.core.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.tudominio.foliabans.core.FoliaBansCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HologramManager {

    private final FoliaBansCore plugin;
    private final ProtocolManager protocolManager;

    public HologramManager(FoliaBansCore plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void showBanHologram(Player player) {
        Location loc = player.getLocation().add(0, 2.5, 0); // Un poco encima de la cabeza
        int entityId = (int) (Math.random() * Integer.MAX_VALUE); // ID de entidad aleatoria
        UUID entityUuid = UUID.randomUUID();

        // 1. Paquete para crear la entidad (Armor Stand)
        PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityId);
        spawnPacket.getUUIDs().write(0, entityUuid);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        spawnPacket.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());

        // 2. Paquete para establecer las propiedades de la entidad (metadatos)
        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, entityId);

        String hologramText = "&c&l¡BANEADO!";
        hologramText = ChatColor.translateAlternateColorCodes('&', hologramText);

        List<WrappedDataValue> metadata = new ArrayList<>();
        metadata.add(new WrappedDataValue(0, WrappedDataValue.Serializer.get(Byte.class), (byte) 0x20)); // Invisible
        metadata.add(new WrappedDataValue(2, WrappedDataValue.Serializer.getChatComponentSerializer(true), com.comphenix.protocol.utility.MinecraftReflection.getChatComponentText(hologramText))); // Nombre custom
        metadata.add(new WrappedDataValue(3, WrappedDataValue.Serializer.get(Boolean.class), true)); // Custom name visible
        metadata.add(new WrappedDataValue(15, WrappedDataValue.Serializer.get(Byte.class), (byte) (0x01 | 0x08 | 0x10))); // Es un Armor Stand pequeño y sin placa base

        metadataPacket.getDataValueCollectionModifier().write(0, metadata);

        try {
            protocolManager.sendServerPacket(player, spawnPacket);
            protocolManager.sendServerPacket(player, metadataPacket);
        } catch (Exception e) {
            plugin.getLogger().warning("No se pudo enviar el paquete de holograma: " + e.getMessage());
        }
    }
}