package com.example.pvpengine.apiKey;

import com.example.pvpengine.apiKey.dto.ApiKeyResponse;
import com.example.pvpengine.apiKey.dto.GenerateApiKeyRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyResponse generateKey(UUID gameId , GenerateApiKeyRequest request);

    ApiKeyResponse revokeKey(UUID keyId , UUID gameId);
    List<ApiKeyResponse> listKeysForGame(UUID gameId);

    Optional<UUID> resolveGameId(String rawKey);
}
