package com.example.pvpengine.matchResult.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class MatchCompleteRequest {

    @NotNull(message = "completedAt is required")
    private OffsetDateTime completedAt;

    @NotEmpty(message = "results are required")
    private List<PlayersResultRequest> results;

    @Builder.Default
    private boolean draw = false;

    @Getter
    public static class PlayersResultRequest {

        @NotNull
        private UUID playerId;
        private int score;
        private boolean winner;
    }
}
