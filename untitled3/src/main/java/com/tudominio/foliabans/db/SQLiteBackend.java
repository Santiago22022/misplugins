package com.tudominio.foliabans.db;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.util.TimeManager;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SQLiteBackend extends StorageBackend {
    private Connection connection;
    private final FoliaBans plugin;

    public SQLiteBackend(FoliaBans plugin) {
        super(plugin);
        this.plugin = plugin;
        setupDatabase();
        createTables();
    }

    private void setupDatabase() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File dbFile = new File(dataFolder, "foliabans.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);

            // Habilitar WAL mode para mejor rendimiento
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL");
                stmt.execute("PRAGMA synchronous=NORMAL");
                stmt.execute("PRAGMA cache_size=10000");
                stmt.execute("PRAGMA temp_store=MEMORY");
            }

            plugin.getLogger().info("Base de datos SQLite conectada correctamente");

        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("Error conectando a SQLite: " + e.getMessage());
        }
    }

    private void createTables() {
        try {
            // Tabla de bans
            String createBansTable = """
                CREATE TABLE IF NOT EXISTS bans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT NOT NULL,
                    reason TEXT NOT NULL,
                    banner TEXT NOT NULL,
                    time INTEGER NOT NULL,
                    until INTEGER NOT NULL DEFAULT 0,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;

            // Tabla de historial
            String createHistoryTable = """
                CREATE TABLE IF NOT EXISTS punishment_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT NOT NULL,
                    type TEXT NOT NULL,
                    reason TEXT NOT NULL,
                    banner TEXT NOT NULL,
                    time INTEGER NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;

            // Índices para mejorar rendimiento
            String createIndexes = """
                CREATE INDEX IF NOT EXISTS idx_bans_uuid ON bans(uuid);
                CREATE INDEX IF NOT EXISTS idx_bans_until ON bans(until);
                CREATE INDEX IF NOT EXISTS idx_history_uuid ON punishment_history(uuid);
                CREATE INDEX IF NOT EXISTS idx_history_time ON punishment_history(time);
                """;

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(createBansTable);
                stmt.executeUpdate(createHistoryTable);

                // Crear índices
                String[] indexes = createIndexes.split(";");
                for (String index : indexes) {
                    if (!index.trim().isEmpty()) {
                        stmt.executeUpdate(index.trim());
                    }
                }
            }

            plugin.getLogger().info("Tablas de SQLite creadas/verificadas correctamente");

        } catch (SQLException e) {
            plugin.getLogger().severe("Error creando tablas SQLite: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<BanInfo> getActiveBan(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String sql = "SELECT reason, until FROM bans WHERE uuid = ? AND (until = 0 OR until > ?) ORDER BY time DESC LIMIT 1";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, uuid.toString());
                    stmt.setLong(2, TimeManager.getTime());

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return new BanInfo(rs.getString("reason"), rs.getLong("until"));
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error obteniendo ban activo: " + e.getMessage());
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> addBan(UUID uuid, String reason, String