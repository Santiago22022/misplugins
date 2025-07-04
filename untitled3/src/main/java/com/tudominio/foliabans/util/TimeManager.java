package com.tudominio.foliabans.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestor de tiempo centralizado para FoliaBans
 */
public class TimeManager {
    
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhdwMy])");
    
    /**
     * Obtiene el timestamp actual en milisegundos
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }

    /**
     * Convierte una cadena de tiempo a milisegundos
     * Formatos soportados: s(segundos), m(minutos), h(horas), d(días), w(semanas), M(meses), y(años)
     */
    public static long toMilliSec(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return 0;
        }

        long totalMillis = 0;
        Matcher matcher = TIME_PATTERN.matcher(timeString.toLowerCase());

        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            totalMillis += switch (unit) {
                case "s" -> amount * 1000L;
                case "m" -> amount * 60L * 1000L;
                case "h" -> amount * 60L * 60L * 1000L;
                case "d" -> amount * 24L * 60L * 60L * 1000L;
                case "w" -> amount * 7L * 24L * 60L * 60L * 1000L;
                case "M" -> amount * 30L * 24L * 60L * 60L * 1000L; // Aproximado
                case "y" -> amount * 365L * 24L * 60L * 60L * 1000L; // Aproximado
                default -> 0L;
            };
        }

        return totalMillis;
    }

    /**
     * Formatea un tiempo en milisegundos a una cadena legible
     */
    public static String formatTime(long millis) {
        if (millis <= 0) return "Permanente";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " día(s)";
        } else if (hours > 0) {
            return hours + " hora(s)";
        } else if (minutes > 0) {
            return minutes + " minuto(s)";
        } else {
            return seconds + " segundo(s)";
        }
    }

    /**
     * Verifica si un tiempo ha expirado
     */
    public static boolean isExpired(long timestamp) {
        return timestamp > 0 && timestamp < getTime();
    }

    public static String formatDuration(long duration) {
    }

    public static long parseTime(@NotNull String arg) {
    }
}