package com.example.pvpengine.kafka;


import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchCompletedEvent {
    private String eventId;
    private String version;
    private UUID matchId;
    private UUID gameId;
    private boolean draw;
    private UUID winnerPlayerId;
    private List<PlayerResult> players;
    private OffsetDateTime occurredAt;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerResult {
        private UUID playerId;
        private int score;
        private boolean winner;
        private int ratingBefore;
    }
}
