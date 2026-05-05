package com.example.pvpengine.matchResult;


import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.matchResult.dto.MatchCompleteRequest;
import com.example.pvpengine.matchResult.dto.MatchStartRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/matches")
@Tag(
        name = "Match Lifecycle",
        description = "Endpoints called by the game server after a matchmaking webhook creates a match. All endpoints require `X-API-KEY` authentication."
)
public class MatchController {

    private final MatchService matchService;

    @Operation(
            summary = "Start match",
            description = "Called by the game server to mark a webhook-created match as started."
    )
    @PostMapping("/{matchId}/start")
    public ResponseEntity<ApiResponse<MatchResponse>> startMatch(
            @Parameter(description = "Unique match ID received from the match-created webhook", example = "de4f8354-1418-4f13-9813-0b00e8efd5b5")
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchStartRequest request){

        return ResponseEntity.ok(ApiResponse.ok(matchService.startMatch(matchId, request) , "Match started"));
    }

    @Operation(
            summary = "Complete match",
            description = "Called by the game server when a match finishes to submit final player results and close the match."
    )
    @PostMapping("/{matchId}/complete")
    public ResponseEntity<ApiResponse<MatchResponse>> completeMatch(
            @Parameter(description = "Unique match ID received from the match-created webhook", example = "de4f8354-1418-4f13-9813-0b00e8efd5b5")
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchCompleteRequest request
    ) {
        return ResponseEntity.accepted()
                .body(ApiResponse.ok(matchService.completeMatch(matchId, request) , "Match result accepted for processing"));
    }
}
