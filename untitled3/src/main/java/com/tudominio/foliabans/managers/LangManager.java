package com.tudominio.foliabans.managers;

import com.tudominio.foliabans.FoliaBans;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangManager {

    private final FoliaBans plugin;
    private FileConfiguration langConfig;

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public LangManager(FoliaBans plugin) {
        this.plugin = plugin;
        loadLanguageFile();
    }

    private void loadLanguageFile() {
        // Primero, nos aseguramos de que los archivos de idioma por defecto existan.
        createDefaultLangFiles();

        // Luego, cargamos el que el usuario ha seleccionado en el config.yml
        String langFileName = plugin.getConfig().getString("settings.language", "es_ES") + ".yml";
        File langFile = new File(plugin.getDataFolder(), "lang/" + langFileName);

        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Comprueba y crea los archivos de idioma por defecto (es_ES y en_US) si no existen.
     */
    private void createDefaultLangFiles() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        createLangFileIfNotExists("es_ES.yml");
        createLangFileIfNotExists("en_US.yml");
    }

    /**
     * Método interno para crear un archivo de idioma con su contenido por defecto si no existe.
     * @param fileName El nombre del archivo (ej: "es_ES.yml")
     */
    private void createLangFileIfNotExists(String fileName) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);
        if (!langFile.exists()) {
            plugin.getLogger().info("Creando archivo de idioma por defecto: " + fileName);
            try {
                List<String> content = getDefaultLangContent(fileName);
                Files.write(langFile.toPath(), content, StandardCharsets.UTF_8);
            } catch (IOException e) {
                plugin.getLogger().severe("¡No se pudo crear el archivo de idioma " + fileName + "! " + e.getMessage());
            }
        }
    }

    /**
     * Devuelve el contenido por defecto para un archivo de idioma específico.
     * @param fileName El nombre del archivo.
     * @return Una lista de strings con el contenido del archivo.
     */
    private List<String> getDefaultLangContent(String fileName) {
        if (fileName.equals("es_ES.yml")) {
            return List.of(
                    "# FoliaBans - Idioma: Español (España)",
                    "General:",
                    "  Prefix: \"&c&lFoliaBans &8&l»\"",
                    "  NoPerms: \"&cNo tienes permiso para usar este comando.\"",
                    "  PlayerNotFound: \"&cNo se pudo encontrar al jugador '%PLAYER%'.\"",
                    "  PlayerOffline: \"&cEl jugador %PLAYER% no está conectado.\"",
                    "  InvalidTime: \"&cFormato de duración inválido: %TIME%. Usa: 1d, 12h, 30m, 10s.\"",
                    "  DatabaseError: \"&cError en la base de datos. Contacta a un administrador.\"",
                    "  CommandUsage: \"&cUso: %USAGE%\"",
                    "",
                    "Ban:",
                    "  Usage: \"&cUso: /ban <jugador> [razón]\"",
                    "  Success: \"&aHas baneado permanentemente a %PLAYER%. Razón: %REASON%\"",
                    "  AlreadyBanned: \"&cEl jugador %PLAYER% ya está baneado.\"",
                    "  Exempt: \"&cNo puedes banear a %PLAYER%\"",
                    "  Layout: |",
                    "    &c&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    "    ",
                    "    &f                    &c&lHAS SIDO BANEADO",
                    "    ",
                    "    &7Razón: &f%REASON%",
                    "    &7Baneado por: &f%OPERATOR%",
                    "    &7Duración: &f%DURATION%",
                    "    ",
                    "    &7Si crees que esto es un error, contacta a los administradores.",
                    "    ",
                    "    &c&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    "  Notification: \"&c%PLAYER% &7ha sido baneado por &e%OPERATOR% &7- Razón: &f%REASON%\"",
                    "",
                    "TempBan:",
                    "  Usage: \"&cUso: /tempban <jugador> <tiempo> [razón]\"",
                    "  Success: \"&aHas baneado temporalmente a %PLAYER% por %DURATION%. Razón: %REASON%\"",
                    "",
                    "Unban:",
                    "  Usage: \"&cUso: /unban <jugador>\"",
                    "  Success: \"&aHas desbaneado a %PLAYER%.\"",
                    "  NotBanned: \"&cEl jugador %PLAYER% no está baneado.\"",
                    "",
                    "Kick:",
                    "  Usage: \"&cUso: /kick <jugador> [razón]\"",
                    "  Success: \"&aHas expulsado a %PLAYER%. Razón: %REASON%\"",
                    "  Message: |",
                    "    &cHas sido expulsado del servidor.",
                    "    &fRazón: &e%REASON%",
                    "",
                    "History:",
                    "  Usage: \"&cUso: /history <jugador>\"",
                    "  Header: \"&6--- [ Historial de Sanciones para %PLAYER% ] ---\"",
                    "  Entry: \"&e- [%TYPE%] &fpor &b%OPERATOR%&f: %REASON% &7- &f%DATE%\"",
                    "  NoHistory: \"&aEl jugador %PLAYER% no tiene historial de sanciones.\""
            );
        } else { // Por defecto, devuelve inglés (en_US.yml)
            return List.of(
                    "# FoliaBans - Language: English (US)",
                    "General:",
                    "  Prefix: \"&c&lFoliaBans &8&l»\"",
                    "  NoPerms: \"&cYou do not have permission to use this command.\"",
                    "  PlayerNotFound: \"&cPlayer '%PLAYER%' could not be found.\"",
                    "  PlayerOffline: \"&cPlayer %PLAYER% is not online.\"",
                    "  InvalidTime: \"&cInvalid duration format: %TIME%. Use: 1d, 12h, 30m, 10s.\"",
                    "  DatabaseError: \"&cDatabase error. Contact an administrator.\"",
                    "  CommandUsage: \"&cUsage: %USAGE%\"",
                    "",
                    "Ban:",
                    "  Usage: \"&cUsage: /ban <player> [reason]\"",
                    "  Success: \"&aYou have permanently banned %PLAYER%. Reason: %REASON%\"",
                    "  AlreadyBanned: \"&cPlayer %PLAYER% is already banned.\"",
                    "  Exempt: \"&cYou cannot ban %PLAYER%\"",
                    "  Layout: |",
                    "    &c&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    "    ",
                    "    &f                    &c&lYOU HAVE BEEN BANNED",
                    "    ",
                    "    &7Reason: &f%REASON%",
                    "    &7Banned by: &f%OPERATOR%",
                    "    &7Duration: &f%DURATION%",
                    "    ",
                    "    &7If you think this is an error, contact the administrators.",
                    "    ",
                    "    &c&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    "  Notification: \"&c%PLAYER% &7has been banned by &e%OPERATOR% &7- Reason: &f%REASON%\"",
                    "",
                    "TempBan:",
                    "  Usage: \"&cUsage: /tempban <player> <time> [reason]\"",
                    "  Success: \"&aYou have temporarily banned %PLAYER% for %DURATION%. Reason: %REASON%\"",
                    "",
                    "Unban:",
                    "  Usage: \"&cUsage: /unban <player>\"",
                    "  Success: \"&aYou have unbanned %PLAYER%.\"",
                    "  NotBanned: \"&cPlayer %PLAYER% is not banned.\"",
                    "",
                    "Kick:",
                    "  Usage: \"&cUsage: /kick <player> [reason]\"",
                    "  Success: \"&aYou have kicked %PLAYER%. Reason: %REASON%\"",
                    "  Message: |",
                    "    &cYou have been kicked from the server.",
                    "    &fReason: &e%REASON%",
                    "",
                    "History:",
                    "  Usage: \"&cUsage: /history <player>\"",
                    "  Header: \"&6--- [ Punishment History for %PLAYER% ] ---\"",
                    "  Entry: \"&e- [%TYPE%] &fby &b%OPERATOR%&f: %REASON% &7- &f%DATE%\"",
                    "  NoHistory: \"&aPlayer %PLAYER% has no punishment history.\""
            );
        }
    }

    public String getMessage(String path) {
        String message = langConfig.getString(path);
        if (message == null) {
            return ChatColor.RED + "Clave de idioma no encontrada: " + path;
        }
        return translateHexColorCodes(message);
    }

    public FileConfiguration getLangFile() {
        return langConfig;
    }

    public FileConfiguration getCurrentLanguage() {
        return langConfig;
    }

    private String translateHexColorCodes(String message) {
        if (message == null) return "";
        message = ChatColor.translateAlternateColorCodes('&', message);
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + group).toString());
        }
        return matcher.appendTail(buffer).toString();
    }
}