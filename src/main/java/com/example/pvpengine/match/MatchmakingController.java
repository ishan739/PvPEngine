package com.example.pvpengine.match;

import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.match.dto.JoinQueueRequest;
import com.example.pvpengine.match.dto.LeaveQueueRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/matchmaking")
@RequiredArgsConstructor
@Tag(
        name = "Matchmaking",
        description = "Manage player queue participation for matchmaking. All endpoints require `X-API-KEY` authentication."
)
public class MatchmakingController {
    private final MatchmakingService matchmakingService;

    @Operation(
            summary = "Join matchmaking queue",
            description = "Adds a player to the matchmaking queue so they can be considered for match creation."
    )
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(
            @Valid @RequestBody JoinQueueRequest request){
        matchmakingService.joinQueue(request);
        return ResponseEntity.ok(ApiResponse.ok(null , "Player added to matchmaking queue"));
    }

    @Operation(
            summary = "Leave matchmaking queue",
            description = "Removes a player from the matchmaking queue, typically when they exit before being matched."
    )
    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leave(
            @Valid @RequestBody LeaveQueueRequest request){
        matchmakingService.leaveQueue(request);
        return ResponseEntity.ok(ApiResponse.ok(null , "Player removed from matchmaking queue"));
    }
}
