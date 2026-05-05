package com.example.pvpengine.player;


import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.player.dto.CreatePlayerRequest;
import com.example.pvpengine.player.dto.PlayerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@Tag(
        name = "Players",
        description = "Create and manage players for the authenticated game. All endpoints require `X-API-KEY` authentication."
)
public class PlayerController {

    private final PlayerService playerService;

    @Operation(
            summary = "Create player",
            description = "Creates a player under the authenticated game. Use `externalPlayerId` to map an existing player identifier from an external or pre-existing game system."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PlayerResponse>> createPlayer(
             @Valid @RequestBody CreatePlayerRequest request
    ){
        PlayerResponse body = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(ApiResponse.ok(body , "Player Created"));
    }

    @Operation(
            summary = "Get player by ID",
            description = "Returns player details for the authenticated game using internal player UUID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getById(
            @Parameter(description = "Internal player UUID", example = "5c31555d-2197-42b1-b4a8-605d1d6e1db9")
            @PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerById(id)));
    }

    @Operation(
            summary = "Get player by username",
            description = "Returns player details for the authenticated game using username."
    )
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getByUsername(
            @Parameter(description = "Player username", example = "pro_striker")
            @PathVariable String username){
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerByUsername(username)));
    }

    @Operation(
            summary = "Get player by external player ID",
            description = "Returns player details using `externalPlayerId`, which is intended for games that already maintain their own player IDs and want to map them directly into PvPEngine."
    )
    @GetMapping("/external/{externalPlayerId}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getByExternalId(
            @Parameter(description = "External player identifier from your game system", example = "steam_76561198000000000")
            @PathVariable String externalPlayerId){
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerByExternalId(externalPlayerId)));
    }

    @Operation(
            summary = "Ban player",
            description = "Bans the player for the authenticated game and stores the provided ban reason."
    )
    @PatchMapping("/{id}/ban")
    public ResponseEntity<ApiResponse<PlayerResponse>> ban(
            @Parameter(description = "Internal player UUID", example = "5c31555d-2197-42b1-b4a8-605d1d6e1db9")
            @PathVariable UUID id,
            @Parameter(description = "Reason for banning the player", example = "Cheating detected")
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.banPlayer(id, reason)));
    }

    @Operation(
            summary = "Unban player",
            description = "Removes ban status from a previously banned player for the authenticated game."
    )
    @PatchMapping("/{id}/unban")
    public ResponseEntity<ApiResponse<PlayerResponse>> unban(
            @Parameter(description = "Internal player UUID", example = "5c31555d-2197-42b1-b4a8-605d1d6e1db9")
            @PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.ok(playerService.unbanPlayer(id)));
    }

}
