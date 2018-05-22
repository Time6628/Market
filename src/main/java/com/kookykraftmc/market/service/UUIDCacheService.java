package com.kookykraftmc.market.service;

import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class UUIDCacheService {
    private Map<UUID, String> uuidCache = new HashMap<>();

    public void update(UUID playerUuid, String playerName) {
        this.uuidCache.put(playerUuid, playerName);
    }

    public String getName(UUID playerUUID) {
        return uuidCache.computeIfAbsent(playerUUID, (uuid) -> getUser(uuid).map(User::getName).orElse("Unknown User"));
    }

    private Optional<User> getUser(UUID uuid) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(uuid);
    }
}
