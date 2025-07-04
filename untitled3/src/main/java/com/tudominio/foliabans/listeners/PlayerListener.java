package com.tudominio.foliabans.listeners;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.db.BanInfo;
import com.tudominio.foliabans.util.TimeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListener implements Listener {
    private final FoliaBans plugin;

    public PlayerListener(FoliaBans plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            // .join() es seguro aquí porque el evento es asíncrono
            BanInfo banInfo = plugin.getStorageManager().getBackend().getActiveBan(event.getUniqueId()).join();

            if (banInfo != null) {
                String duration = banInfo.getUntil() == -1L ? "Permanente" : TimeManager.formatDuration(banInfo.getUntil() - System.currentTimeMillis());
                String msg = plugin.getLangManager().getMessage("ban-login-message")
                        .replace("%reason%", banInfo.getReason())
                        .replace("%duration%", duration);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error al verificar el baneo de " + event.getName() + ": " + e.getMessage());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Error al verificar tu estado."));
        }
    }
}