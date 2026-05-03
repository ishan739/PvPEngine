package com.example.pvpengine.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MatchFoundPayload {
    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("matchId")
    private UUID matchId;

    @JsonProperty("gameId")
    private UUID gameId;

    @JsonProperty("players")
    private List<PlayerInfo> players;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;

    @Getter
    @Builder
    public static class PlayerInfo {

        @JsonProperty("playerId")
        private UUID playerId;

        @JsonProperty("rating")
        private int rating;
    }

}
