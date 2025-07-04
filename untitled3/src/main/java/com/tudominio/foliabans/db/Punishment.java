package com.tudominio.foliabans.db;

import java.util.UUID;

/**
 * Record que representa un castigo en FoliaBans
 */
public record Punishment(
        UUID uuid,
        Type type,
        String reason,
        String banner,
        long time
) {

    public enum Type {
        BAN("Ban"),
        TEMPBAN("Tempban"),
        KICK("Kick"),
        MUTE("Mute"),
        TEMPMUTE("Tempmute"),
        WARNING("Warning"),
        TEMPWARNING("Tempwarning"),
        UNBAN("Unban"),
        UNMUTE("Unmute"),
        NOTE("Note");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        // Método para compatibilidad
        public String getName() {
            return displayName;
        }
    }
}