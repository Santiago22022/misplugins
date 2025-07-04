package com.tudominio.foliabans.managers;

import com.tudominio.foliabans.FoliaBans;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Gestor de mensajes mejorado con soporte para Adventure API
 */
public class MessageManager {
    private final FoliaBans plugin;
    private final LegacyComponentSerializer serializer;

    public MessageManager(FoliaBans plugin) {
        this.plugin = plugin;
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
    }

    /**
     * Obtiene un mensaje desde el archivo de idioma
     */
    public String getMessage(String path, Object... parameters) {
        return getMessage(path, true, parameters);
    }

    /**
     * Obtiene un mensaje con opción de prefijo
     */
    public String getMessage(String path, boolean prefix, Object... parameters) {
        FileConfiguration lang = plugin.getLangManager().getCurrentLanguage();
        String message = lang.getString(path, "§cMensaje no encontrado: " + path);
        
        // Reemplazar parámetros
        for (int i = 0; i < parameters.length; i += 2) {
            if (i + 1 < parameters.length) {
                String placeholder = "%" + parameters[i] + "%";
                String value = String.valueOf(parameters[i + 1]);
                message = message.replace(placeholder, value);
            }
        }

        // Agregar prefijo si es necesario
        if (prefix && !lang.getBoolean("General.DisablePrefix", false)) {
            String prefixMsg = lang.getString("General.Prefix", "&c&lFoliaBans &8&l»");
            message = prefixMsg + " " + message;
        }

        return message;
    }

    /**
     * Obtiene una lista de mensajes (para layouts)
     */
    public List<String> getLayout(String path, Object... parameters) {
        FileConfiguration lang = plugin.getLangManager().getCurrentLanguage();
        List<String> layout = lang.getStringList(path);
        
        if (layout.isEmpty()) {
            layout = List.of("§cLayout no encontrado: " + path);
        }

        // Procesar cada línea del layout
        return layout.stream()
                .map(line -> {
                    // Reemplazar parámetros
                    for (int i = 0; i < parameters.length; i += 2) {
                        if (i + 1 < parameters.length) {
                            String placeholder = "%" + parameters[i] + "%";
                            String value = String.valueOf(parameters[i + 1]);
                            line = line.replace(placeholder, value);
                        }
                    }
                    return line;
                })
                .toList();
    }

    /**
     * Convierte texto legacy a Component
     */
    public Component toComponent(String text) {
        return serializer.deserialize(text);
    }

    /**
     * Envía notificaciones a jugadores con permisos
     */
    public void sendNotification(String permission, String messageKey, Object... parameters) {
        String message = getMessage(messageKey, parameters);
        plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission(permission))
                .forEach(player -> player.sendMessage(message));
    }
}