package com.tudominio.foliabans.db;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageBackend {
    boolean init();
    void shutdown();
    
    // --- Métodos de Ban ---
    void addBan(UUID uuid, String reason, String banner, long until);
    CompletableFuture<Boolean> removeBan(UUID uuid);
    CompletableFuture<BanInfo> getActiveBan(UUID uuid);
    
    // --- Métodos de IP Ban ---
    void addIpBan(String ip, String reason, String banner, long until);
    CompletableFuture<Boolean> removeIpBan(String ip);
    CompletableFuture<BanInfo> getActiveIpBan(String ip);
    
    // --- Métodos de Mute ---
    void addMute(UUID uuid, String reason, String banner, long until);
    CompletableFuture<Boolean> removeMute(UUID uuid);
    CompletableFuture<BanInfo> getActiveMute(UUID uuid);
    
    // --- Métodos de Warning ---
    void addWarning(UUID uuid, String reason, String banner, long until);
    CompletableFuture<List<BanInfo>> getActiveWarnings(UUID uuid);
    CompletableFuture<Integer> getWarningCount(UUID uuid);
    
    // --- Métodos de Historial ---
    void logPunishment(Punishment punishment);
    CompletableFuture<List<Punishment>> getHistory(UUID uuid);
    CompletableFuture<List<Punishment>> getAllPunishments(int limit);
    
    // --- Métodos de búsqueda ---
    CompletableFuture<Punishment> getPunishmentById(int id);
    CompletableFuture<List<Punishment>> searchPunishments(String query);
    
    // --- Limpieza automática ---
    CompletableFuture<Integer> cleanExpiredPunishments();
}