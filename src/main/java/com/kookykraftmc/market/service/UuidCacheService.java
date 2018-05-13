package com.kookykraftmc.market.service;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class UuidCacheService {
    private Map<UUID, String> uuidCache = new HashMap<>();

    public void update(UUID playerUuid, String playerName) {
        this.uuidCache.put(playerUuid, playerName);
    }

    public String getName(UUID playerUuid) {
        return uuidCache.get(playerUuid);
    }
}
