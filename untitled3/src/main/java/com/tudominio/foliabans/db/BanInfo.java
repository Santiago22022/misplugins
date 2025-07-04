// Archivo: BanInfo.java (ACTUALIZADO)
// Ubicación: src/main/java/com/tudominio/foliabans/db/BanInfo.java
package com.tudominio.foliabans.db;

import com.tudominio.foliabans.util.TimeManager;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Información de un ban activo.
 */
public final class BanInfo {
    private final String reason;
    private final long until; // Cambio a long para compatibilidad

    /**
     * Crea una nueva instancia de información de ban.
     *
     * @param reason Motivo del ban.
     * @param until  Timestamp de expiración (0 para ban permanente).
     */
    public BanInfo(String reason, long until) {
        this.reason = Objects.requireNonNull(reason, "reason no puede ser null");
        this.until = until;
    }

    public String getReason() {
        return reason;
    }

    /**
     * @return Timestamp en que expira el ban, 0 si es permanente.
     */
    public long getUntil() {
        return until;
    }

    /**
     * @return true si es un ban permanente
     */
    public boolean isPermanent() {
        return until <= 0;
    }

    /**
     * Comprueba si el ban ha expirado.
     *
     * @return true si el tiempo actual supera until, false en otro caso o si es permanente.
     */
    public boolean isExpired() {
        return !isPermanent() && until < TimeManager.getTime();
    }

    /**
     * @return Duración restante en milisegundos, 0 si es permanente o expirado
     */
    public long getRemainingTime() {
        if (isPermanent() || isExpired()) {
            return 0;
        }
        return until - TimeManager.getTime();
    }
}