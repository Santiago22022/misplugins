package com.tudominio.foliabans.db;

import com.tudominio.foliabans.FoliaBans;
import com.tudominio.foliabans.util.TimeManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQLBackend extends StorageBackend {
    private HikariDataSource dataSource;

    public MySQLBackend(FoliaBans plugin) {
        super(plugin);
        setupDataSource();
        createTables();
    }

    private void setupDataSource() {
        HikariConfig config = new HikariConfig();

        String host = plugin.getConfig().getString("database.host", "localhost");
        int port = plugin.getConfig().getInt("database.port", 3306);
        String database = plugin.getConfig().getString("database.database", "foliabans");
        String username = plugin.getConfig().getString("database.username", "root");
        String password = plugin.getConfig().getString("database.password", "");

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Configuraciones de conexión
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(config);
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection()) {
            // Tabla de bans
            String createBansTable = """
                CREATE TABLE IF NOT EXISTS bans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    uuid VARCHAR(36) NOT NULL,
                    reason TEXT NOT NULL,
                    banner VARCHAR(255) NOT NULL,
                    time BIGINT NOT NULL,
                    until BIGINT NOT NULL DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_uuid (uuid),
                    INDEX idx_until (until)
                )
                """;

            // Tabla de historial
            String createHistoryTable = """
                CREATE TABLE IF NOT EXISTS punishment_history (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    uuid VARCHAR(36) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    reason TEXT NOT NULL,
                    banner VARCHAR(255) NOT NULL,
                    time BIGINT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_uuid (uuid),
                    INDEX idx_time (time)
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createBansTable);
                stmt.executeUpdate(createHistoryTable);
            }

            plugin.getLogger().info("Tablas de MySQL creadas/verificadas correctamente");

        } catch (SQLException e) {
            plugin.getLogger().severe("Error creando tablas MySQL: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<BanInfo> getActiveBan(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "SELECT reason, until FROM bans WHERE uuid = ? AND (until = 0 OR until > ?) ORDER BY time DESC LIMIT 1";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
    public CompletableFuture<Void> addBan(UUID uuid, String reason, String banner, long until) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "INSERT INTO bans (uuid, reason, banner, time, until) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, uuid.toString());
                    stmt.setString(2, reason);
                    stmt.setString(3, banner);
                    stmt.setLong(4, TimeManager.getTime());
                    stmt.setLong(5, until);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error añadiendo ban: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> removeBan(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "DELETE FROM bans WHERE uuid = ? AND (until = 0 OR until > ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, uuid.toString());
                    stmt.setLong(2, TimeManager.getTime());
                    return stmt.executeUpdate() > 0;
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error removiendo ban: " + e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Void> logPunishment(Punishment punishment) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "INSERT INTO punishment_history (uuid, type, reason, banner, time) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, punishment.uuid().toString());
                    stmt.setString(2, punishment.type().name());
                    stmt.setString(3, punishment.reason());
                    stmt.setString(4, punishment.banner());
                    stmt.setLong(5, punishment.time());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error registrando castigo: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<List<Punishment>> getHistory(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<Punishment> history = new ArrayList<>();
            try (Connection conn = dataSource.getConnection()) {
                String sql = "SELECT type, reason, banner, time FROM punishment_history WHERE uuid = ? ORDER BY time DESC LIMIT 50";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, uuid.toString());

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Punishment.Type type = Punishment.Type.valueOf(rs.getString("type"));
                            history.add(new Punishment(
                                    uuid,
                                    type,
                                    rs.getString("reason"),
                                    rs.getString("banner"),
                                    rs.getLong("time")
                            ));
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error obteniendo historial: " + e.getMessage());
            }
            return history;
        });
    }

    @Override
    public CompletableFuture<Integer> cleanExpiredPunishments() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "DELETE FROM bans WHERE until > 0 AND until < ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, TimeManager.getTime());
                    return stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error limpiando castigos expirados: " + e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}