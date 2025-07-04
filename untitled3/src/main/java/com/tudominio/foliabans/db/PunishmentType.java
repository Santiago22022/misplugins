package com.tudominio.foliabans.db;

/**
 * Tipos de castigos disponibles en FoliaBans
 */
public enum PunishmentType {
    BAN("Ban", null, false, "foliabans.ban"),
    TEMP_BAN("Tempban", BAN, true, "foliabans.tempban"),
    IP_BAN("Ipban", BAN, false, "foliabans.ipban"),
    TEMP_IP_BAN("Tempipban", BAN, true, "foliabans.tempipban"),
    MUTE("Mute", null, false, "foliabans.mute"),
    TEMP_MUTE("Tempmute", MUTE, true, "foliabans.tempmute"),
    WARNING("Warn", null, false, "foliabans.warn"),
    TEMP_WARNING("Tempwarn", WARNING, true, "foliabans.tempwarn"),
    KICK("Kick", null, false, "foliabans.kick"),
    NOTE("Note", null, false, "foliabans.note");

    private final String name;
    private final String perms;
    private final PunishmentType basic;
    private final boolean temp;

    PunishmentType(String name, PunishmentType basic, boolean temp, String perms) {
        this.name = name;
        this.basic = basic;
        this.temp = temp;
        this.perms = perms;
    }

    public static PunishmentType fromCommandName(String cmd) {
        return switch (cmd.toLowerCase()) {
            case "ban" -> BAN;
            case "tempban" -> TEMP_BAN;
            case "ipban", "banip" -> IP_BAN;
            case "tempipban" -> TEMP_IP_BAN;
            case "mute" -> MUTE;
            case "tempmute" -> TEMP_MUTE;
            case "warn" -> WARNING;
            case "tempwarn" -> TEMP_WARNING;
            case "kick" -> KICK;
            case "note" -> NOTE;
            default -> null;
        };
    }

    public String getName() {
        return name;
    }

    public String getPerms() {
        return perms;
    }

    public boolean isTemp() {
        return temp;
    }

    public PunishmentType getBasic() {
        return basic;
    }

    public String getConfSection(String section) {
        return name + "." + section;
    }
}