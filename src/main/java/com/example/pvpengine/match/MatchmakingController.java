package com.example.pvpengine.match;

import com.example.pvpengine.common.response.ApiResponse;
import com.example.pvpengine.match.dto.JoinQueueRequest;
import com.example.pvpengine.match.dto.LeaveQueueRequest;
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
public class MatchmakingController {
    private final MatchmakingService matchmakingService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(
            @Valid @RequestBody JoinQueueRequest request){
        matchmakingService.joinQueue(request);
        return ResponseEntity.ok(ApiResponse.ok(null , "Player added to matchmaking queue"));
    }

    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leave(
            @Valid @RequestBody LeaveQueueRequest request){
        matchmakingService.leaveQueue(request);
        return ResponseEntity.ok(ApiResponse.ok(null , "Player removed from matchmaking queue"));
    }
}
