package com.example.pvpengine.player;


import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.player.dto.CreatePlayerRequest;
import com.example.pvpengine.player.dto.PlayerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlayerResponse>> createPlayer(
            @Valid @RequestBody CreatePlayerRequest request
    ){
        PlayerResponse body = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(ApiResponse.ok(body , "Player Created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getById(@PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getByUsername(@PathVariable String username){
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerByUsername(username)));
    }

    @GetMapping("/external/{externalPlayerId}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getByExternalId(
            @PathVariable String externalPlayerId){
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerByExternalId(externalPlayerId)));
    }

    @PatchMapping("/{id}/ban")
    public ResponseEntity<ApiResponse<PlayerResponse>> ban(
            @PathVariable UUID id,
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.banPlayer(id, reason)));
    }

    @PatchMapping("/{id}/unban")
    public ResponseEntity<ApiResponse<PlayerResponse>> unban(@PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.ok(playerService.unbanPlayer(id)));
    }

}
