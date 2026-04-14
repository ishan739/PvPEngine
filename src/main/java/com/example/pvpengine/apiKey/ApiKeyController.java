package com.example.pvpengine.apiKey;

import com.example.pvpengine.apiKey.dto.ApiKeyResponse;
import com.example.pvpengine.apiKey.dto.GenerateApiKeyRequest;
import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.game.GameServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games/{gameId}/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ResponseEntity<ApiResponse<ApiKeyResponse>> generateKey(
            @PathVariable UUID gameid,
            @RequestBody(required = false) GenerateApiKeyRequest request
            ) {
        ApiKeyResponse body = apiKeyService.generateKey(gameid,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok(body , "Api key generated. Store raw key securely- it will never be shown again")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> listKeys(
            @PathVariable UUID gameId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(apiKeyService.listKeysForGame(gameId)));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<ApiResponse<ApiKeyResponse>> revokeKey(
            @PathVariable UUID gameId,
            @PathVariable UUID keyId
    ){
        return ResponseEntity.ok(ApiResponse.ok(apiKeyService.revokeKey(gameId,keyId) , "Api key revoked"));
    }
}
