// Archivo: RevokePunishmentEvent.java
// Ubicación: src/main/java/com/tudominio/foliabans/events/RevokePunishmentEvent.java
package com.tudominio.foliabans.events;

import com.tudominio.foliabans.db.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando se revoca un castigo
 */
public class RevokePunishmentEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Punishment punishment;
    private final boolean massClear;

    public RevokePunishmentEvent(Punishment punishment, boolean massClear) {
        this.punishment = punishment;
        this.massClear = massClear;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    public boolean isMassClear() {
        return massClear;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}