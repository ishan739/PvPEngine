package com.example.pvpengine.game;

import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.game.dto.CreateGameRequest;
import com.example.pvpengine.game.dto.GameResponse;
import com.example.pvpengine.game.dto.UpdateWebhookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Tag(name = "Games", description = "Game registration, lookup, webhook configuration, and lifecycle controls.")
public class GameController {

    private final GameService gameService;

    @Operation(
            summary = "Create a game",
            description = "Creates a new game and returns its details, including generated identifiers and current lifecycle status."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<GameResponse>> createGame(
            @Valid @RequestBody CreateGameRequest request) {
        GameResponse body =  gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(body, "Game created successfully"));
    }

    @Operation(
            summary = "Get game by ID",
            description = "Fetches the details of a single game using its UUID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameResponse>> getById(
            @Parameter(description = "Unique game ID", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.getGameById(id)));
    }

    @Operation(
            summary = "Get game by code",
            description = "Fetches a game using its public code value."
    )
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<GameResponse>> getByCode(
            @Parameter(description = "Game code created during game registration", example = "arena-duel")
            @PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.getGameByCode(code)));
    }

    @Operation(
            summary = "Update webhook URL",
            description = "Updates the destination URL used for outbound webhook events for a specific game."
    )
    @PostMapping("/{id}/webhook")
    public ResponseEntity<ApiResponse<GameResponse>> updateWebhook(
            @Parameter(description = "Unique game ID", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWebhookRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.updateWebhookUrl(id, request)));
    }

    @Operation(
            summary = "Suspend game",
            description = "Moves a game to suspended state so it can be temporarily disabled."
    )
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<GameResponse>> suspend(
            @Parameter(description = "Unique game ID", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.suspendGame(id)));
    }

    @Operation(
            summary = "Activate game",
            description = "Reactivates a previously suspended game."
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<GameResponse>> activate(
            @Parameter(description = "Unique game ID", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gameService.activateGame(id)));
    }

}
