package com.example.pvpengine.leaderboard;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LeaderboardEntry {
    private int rank;
    private UUID playerId;
    private int rating;
}
