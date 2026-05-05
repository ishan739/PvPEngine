package com.example.pvpengine.leaderboard;

import com.example.pvpengine.common.TenantContext;
import com.example.pvpengine.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:";
    private static int MAX_TOP_N = 100;

    private final RedisTemplate<String , String> redisTemplate;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getTopPlayers(
            @RequestParam(defaultValue = "10") int limit){
        UUID gameId = TenantContext.requireGameId();

        limit = Math.min(limit, MAX_TOP_N);
        limit = Math.max(limit, 1);

        String key = LEADERBOARD_KEY_PREFIX + gameId;

        Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit-1);

        if (entries == null || entries.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(List.of(), "Leaderboard is Empty"));
        }

        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        int rank=  1;
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            leaderboard.add(LeaderboardEntry.builder()
                    .rank(rank++)
                    .playerId(UUID.fromString(Objects.requireNonNull(entry.getValue())))
                    .rating((int) Math.round(Objects.requireNonNull(entry.getScore())))
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.ok(leaderboard));
    }
}
