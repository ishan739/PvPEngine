package com.example.pvpengine.apiKey.dto;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class GenerateApiKeyRequest {
    private OffsetDateTime expiresAt;
}
