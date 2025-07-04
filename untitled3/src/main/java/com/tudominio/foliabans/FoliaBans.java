// Archivo: FoliaBans.java (ACTUALIZADO)
// Ubicación: src/main/java/com/tudominio/foliabans/FoliaBans.java
package com.tudominio.foliabans;

import com.tudominio.foliabans.commands.*;
import com.tudominio.foliabans.db.StorageManager;
import com.tudominio.foliabans.listeners.PlayerListener;
import com.tudominio.foliabans.managers.CoreManager;
import com.tudominio.foliabans.managers.LangManager;
import com.tudominio.foliabans.managers.MessageManager;
import com.tudominio.foliabans.util.SchedulerAdapter;
import com.tudominio.foliabans.util.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Level;

public final class FoliaBans extends JavaPlugin {

    private StorageManager storageManager;
    private LangManager langManager;
    private MessageManager messageManager;
    private SchedulerAdapter schedulerAdapter;
    private CoreManager coreManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Inicializar managers en orden
        this.schedulerAdapter = new SchedulerAdapter(this);
        this.langManager = new LangManager(this);
        this.messageManager = new MessageManager(this);
        this.coreManager = new CoreManager(this);
        this.storageManager = new StorageManager(this);

        if (!storageManager.init()) {
            getLogger().severe("No se pudo inicializar el backend de almacenamiento, desactivando plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Registrar listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        // Registrar comandos
        registerCommands();

        // Iniciar tareas programadas si está habilitado
        if (getConfig().getBoolean("cleanup.auto-cleanup", true)) {
            startCleanupTask();
        }

        getLogger().info("FoliaBans cargado correctamente con todas las mejoras.");
    }

    private void registerCommands() {
        CommandMap map;
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
            map = (CommandMap) commandMap.get(Bukkit.getServer());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "No se pudo registrar comandos.", e);
            return;
        }

        // Registrar comandos mejorados
        map.register("foliabans", new BanCommand(this));
        map.register("foliabans", new TempbanCommand(this));
        map.register("foliabans", new UnbanCommand(this));
        map.register("foliabans", new KickCommand(this));
        map.register("foliabans", new HistoryCommand(this));
    }

    private void startCleanupTask() {
        int intervalMinutes = getConfig().getInt("cleanup.interval", 30);
        long intervalTicks = intervalMinutes * 20L * 60L; // Convertir minutos a ticks

        schedulerAdapter.runTaskLater(() -> {
            // Limpiar castigos expirados
            storageManager.getBackend().cleanExpiredPunishments()
                    .thenAccept(cleaned -> {
                        if (cleaned > 0) {
                            getLogger().info("Limpieza automática: " + cleaned + " castigos expirados eliminados.");
                        }
                    });

            // Reprogramar la tarea
            startCleanupTask();
        }, intervalTicks);
    }

    @Override
    public void onDisable() {
        if (storageManager != null) storageManager.shutdown();
        getLogger().info("FoliaBans deshabilitado correctamente.");
    }

    // Getters actualizados
    public StorageManager getStorageManager() { return storageManager; }
    public LangManager getLangManager() { return langManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public SchedulerAdapter getSchedulerAdapter() { return schedulerAdapter; }
    public CoreManager getCoreManager() { return coreManager; }
}