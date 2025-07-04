package com.tudominio.foliabans.core.listeners;

import com.tudominio.foliabans.core.hologram.HologramManager;
import com.tudominio.foliabans.events.PlayerBannedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BanListener implements Listener {

    private final HologramManager hologramManager;

    public BanListener(HologramManager hologramManager) {
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onPlayerBanned(PlayerBannedEvent event) {
        // Cuando FoliaBans lanza el evento, el Core reacciona.
        hologramManager.showBanHologram(event.getBannedPlayer());
    }
}