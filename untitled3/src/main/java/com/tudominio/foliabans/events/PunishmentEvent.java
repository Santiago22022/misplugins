
package com.tudominio.foliabans.events;

import com.tudominio.foliabans.db.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando se crea un castigo
 */
public class PunishmentEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Punishment punishment;
    private final boolean silent;

    public PunishmentEvent(Punishment punishment, boolean silent) {
        this.punishment = punishment;
        this.silent = silent;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    public boolean isSilent() {
        return silent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}