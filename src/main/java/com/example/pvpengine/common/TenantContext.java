package com.example.pvpengine.common;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<UUID> GAME_ID = new ThreadLocal<>();
    private TenantContext() {}

    public static void setGameId(UUID gameId) {
        GAME_ID.set(gameId);
    }
    public static UUID getGameId() {
        return GAME_ID.get();
    }

    public static UUID requireGameId() {
        UUID gameId = GAME_ID.get();
        if(gameId == null) {
            throw new IllegalStateException("TenantContext has no gameId - request did not pass through ApiKeyAuthFilter");
        }
        return gameId;
    }

    public static void clear() {
        GAME_ID.remove();
    }
}
