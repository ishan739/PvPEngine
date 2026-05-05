package com.example.pvpengine.apiKey;

import com.example.pvpengine.apiKey.dto.ApiKeyResponse;
import com.example.pvpengine.apiKey.dto.GenerateApiKeyRequest;
import com.example.pvpengine.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games/{gameId}/keys")
@RequiredArgsConstructor
@Tag(
        name = "API Keys",
        description = "Generate and manage per-game API keys used to authenticate calls to protected PvPEngine APIs."
)
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @Operation(
            summary = "Generate API key",
            description = "Creates a new API key for a game. The `rawApiKey` returned in the response is shown only once and must be stored securely, because it is required to call protected endpoints such as player, leaderboard, match and matchmaking APIs."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ApiKeyResponse>> generateKey(
            @Parameter(description = "Unique game ID that owns the API key", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID gameId,
            @RequestBody(required = false) GenerateApiKeyRequest request
            ) {
        ApiKeyResponse body = apiKeyService.generateKey(gameId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok(body , "Api key generated. Store raw key securely- it will never be shown again")
        );
    }

    @Operation(
            summary = "List API keys",
            description = "Returns all API keys created for the specified game."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> listKeys(
            @Parameter(description = "Unique game ID whose API keys should be listed", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID gameId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(apiKeyService.listKeysForGame(gameId)));
    }

    @Operation(
            summary = "Revoke API key",
            description = "Revokes a specific API key for a game. Revoked keys can no longer be used for authentication."
    )
    @DeleteMapping("/{keyId}")
    public ResponseEntity<ApiResponse<ApiKeyResponse>> revokeKey(
            @Parameter(description = "Unique game ID that owns the API key", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID gameId,
            @Parameter(description = "Unique API key ID to revoke", example = "f3f6e0e6-f77e-4a22-a8e5-4b9c86304bb3")
            @PathVariable UUID keyId
    ){
        return ResponseEntity.ok(ApiResponse.ok(apiKeyService.revokeKey(gameId,keyId) , "Api key revoked"));
    }
}
