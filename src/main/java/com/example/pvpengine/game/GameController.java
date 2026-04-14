package com.example.pvpengine.game;

import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.game.dto.CreateGameRequest;
import com.example.pvpengine.game.dto.GameResponse;
import com.example.pvpengine.game.dto.UpdateWebhookRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<ApiResponse<GameResponse>> createGame(
           @Valid @RequestBody CreateGameRequest request) {
        GameResponse body =  gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(body, "Game created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.getGameById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<GameResponse>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.getGameByCode(code)));
    }

    @PostMapping("/{id}/webhook")
    public ResponseEntity<ApiResponse<GameResponse>> updateWebhook(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWebhookRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.updateWebhookUrl(id, request)));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<GameResponse>> suspend(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.suspendGame(id)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<GameResponse>> activate(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.activateGame(id)));
    }

}
