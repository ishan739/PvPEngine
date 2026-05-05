package com.example.pvpengine.player.dto;


import com.example.pvpengine.player.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Player details for the authenticated game.")
public class PlayerResponse {
    @Schema(description = "Internal player UUID.", example = "5c31555d-2197-42b1-b4a8-605d1d6e1db9")
    private UUID id;
    @Schema(description = "Game UUID that owns this player.", example = "3f51fc0a-1f28-4b68-8b30-c67fd5f6ccef")
    private UUID gameId;
    @Schema(description = "External player ID from your own game system, when provided.", example = "steam_76561198000000000")
    private String externalPlayerId;
    @Schema(description = "Current MMR/rating of the player.", example = "1200")
    private int rating;
    @Schema(description = "Player username.", example = "pro_striker")
    private String username;
    @Schema(description = "Player email address.", example = "player1@arena.gg")
    private String email;
    @Schema(description = "When the player was created.", example = "2026-05-05T13:40:00Z")
    private OffsetDateTime createdAt;
    @Schema(description = "Last active time of the player.", example = "2026-05-05T14:10:00Z")
    private OffsetDateTime lastActiveAt;
    @Schema(description = "Indicates whether the player is currently banned.", example = "false")
    private boolean banned;
    @Schema(description = "Reason for ban when `banned=true`.", example = "Cheating detected")
    private String banReason;

    public static PlayerResponse from(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .gameId(player.getGameId())
                .externalPlayerId(player.getExternalPlayerId())
                .rating(player.getRating())
                .username(player.getUsername())
                .email(player.getEmail())
                .createdAt(player.getCreatedAt())
                .lastActiveAt(player.getLastActiveAt())
                .banned(player.isBanned())
                .banReason(player.getBanReason())
                .build();
    }
}
