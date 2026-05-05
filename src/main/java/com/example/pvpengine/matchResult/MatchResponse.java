package com.example.pvpengine.matchResult;

import com.example.pvpengine.match.Match;
import com.example.pvpengine.match.MatchStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class MatchResponse {
    private UUID id;
    private UUID gameId;
    private UUID player1Id;
    private UUID player2Id;
    private MatchStatus status;
    private String gameSessionid;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;

    public static MatchResponse from(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .gameId(match.getGameId())
                .player1Id(match.getPlayer1Id())
                .player2Id(match.getPlayer2Id())
                .status(match.getStatus())
                .gameSessionid(match.getGameSessionid())
                .createdAt(match.getCreatedAt())
                .startedAt(match.getStartedAt())
                .completedAt(match.getCompletedAt())
                .build();
    }
}
