package com.example.pvpengine.apiKey.dto;

import com.example.pvpengine.apiKey.ApiKeyCredential;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ApiKeyResponse {
    private UUID id;
    private UUID gameId;
    private String keyPrefix;
    private boolean active;
    private OffsetDateTime lastUsedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;

    private String rawKey;

    public static ApiKeyResponse from(ApiKeyCredential credential){
        return ApiKeyResponse.builder()
                .id(credential.getId())
                .gameId(credential.getGameId())
                .keyPrefix(credential.getKeyPrefix())
                .active(credential.isActive())
                .active(credential.isActive())
                .createdAt(credential.getCreatedAt())
                .expiresAt(credential.getExpiresAt())
                .lastUsedAt(credential.getLastUsedAt())
                .build();
    }

    public static ApiKeyResponse fromWithRawKey(ApiKeyCredential credential, String rawKey) {
        ApiKeyResponse response = from(credential);
        return ApiKeyResponse.builder()
                .id(response.getId())
                .gameId(response.getGameId())
                .keyPrefix(response.getKeyPrefix())
                .active(response.isActive())
                .createdAt(response.getCreatedAt())
                .expiresAt(response.getExpiresAt())
                .lastUsedAt(response.getLastUsedAt())
                .rawKey(rawKey)
                .build();
    }
}
