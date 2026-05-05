package com.example.pvpengine.matchResult;


import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.matchResult.dto.MatchCompleteRequest;
import com.example.pvpengine.matchResult.dto.MatchStartRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/matches")
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/{matchId}/start")
    public ResponseEntity<ApiResponse<MatchResponse>> startMatch(
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchStartRequest request){

        return ResponseEntity.ok(ApiResponse.ok(matchService.startMatch(matchId, request) , "Match started"));
    }

    @PostMapping("/{matchId}/complete")
    public ResponseEntity<ApiResponse<MatchResponse>> completeMatch(
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchCompleteRequest request
    ) {
        return ResponseEntity.accepted()
                .body(ApiResponse.ok(matchService.completeMatch(matchId, request) , "Match result accepted for processing"));
    }
}
