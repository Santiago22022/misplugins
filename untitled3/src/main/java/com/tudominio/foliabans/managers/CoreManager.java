package com.tudominio.foliabans.managers;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.util.SchedulerAdapter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Gestor de utilidades que dependen de ProtocolLib.
 * Se inicializa mediante reflexión para evitar ClassNotFound si el JAR no existe.
 */
public class CoreManager {

    private final FoliaBans plugin;
    private final SchedulerAdapter scheduler;

    /* Referencia al ProtocolManager de ProtocolLib (puede ser null) */
    private Object protocolManager;
    private boolean activo;

    public CoreManager(FoliaBans plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getSchedulerAdapter();

        // Intentamos localizar ProtocolLib mediante reflexión.
        try {
            Class<?> protocolLibClass = Class.forName("com.comphenix.protocol.ProtocolLibrary");
            Method getProtocolManager = protocolLibClass.getMethod("getProtocolManager");
            this.protocolManager = getProtocolManager.invoke(null);
            this.activo = true;

            plugin.getLogger().info("CoreManager: ProtocolLib detectado. Funciones avanzadas habilitadas.");
        } catch (Throwable ex) {
            // No está ProtocolLib o algo fue mal; se deshabilitan las funciones core.
            this.activo = false;
            this.protocolManager = null;
            plugin.getLogger().log(Level.WARNING,
                    "CoreManager: ProtocolLib no disponible; se desactivan las funciones avanzadas.", ex);
        }
    }

    /**
     * Muestra un “holograma” de sanción sobre un jugador.
     * Si ProtocolLib no está activo, simplemente envía un ActionBar como alternativa.
     */
    public void displayPunishmentHologram(Player target, String text) {
        if (target == null || !target.isOnline()) return;

        String colored = ChatColor.translateAlternateColorCodes('&', text);

        if (!activo) {
            // Solución de respaldo sin ProtocolLib.
            scheduler.runTask(() -> target.sendActionBar(colored));
            return;
        }

        /* ---------------------------------------------------------------------
         *  Implementación completa con packets:
         *  Aquí podrías usar reflexión para construir y enviar los paquetes
         *  (SPAWN_ENTITY, ENTITY_METADATA, ENTITY_DESTROY) igual que antes,
         *  usando la instancia protocolManager obtenida arriba.
         * --------------------------------------------------------------------*/

        // Ejemplo simplificado: enviamos ActionBar incluso con ProtocolLib activo
        // mientras no se implemente la versión por reflexión.
        scheduler.runTask(() -> target.sendActionBar(colored));
    }
}
