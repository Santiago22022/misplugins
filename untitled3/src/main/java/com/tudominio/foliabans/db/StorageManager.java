package com.tudominio.foliabans.db;

import com.tudominio.foliabans.FoliaBans;
import org.bukkit.configuration.file.FileConfiguration;

public class StorageManager {
    private final FoliaBans plugin;
    private StorageBackend backend;

    public StorageManager(FoliaBans plugin) {
        this.plugin = plugin;
    }

    public boolean init() {
        FileConfiguration config = plugin.getConfig();
        String storageMethod = config.getString("storage.method", "sqlite").toLowerCase();
        plugin.getLogger().info("Cargando método de almacenamiento: " + storageMethod);

        if (storageMethod.equals("mysql")) {
            this.backend = new MySQLBackend(plugin);
        } else {
            this.backend = new SQLiteBackend(plugin);
        }
        return this.backend.init();
    }

    public StorageBackend getBackend() {
        return backend;
    }

    public void shutdown() {
        if (backend != null) {
            backend.shutdown();
        }
    }
}