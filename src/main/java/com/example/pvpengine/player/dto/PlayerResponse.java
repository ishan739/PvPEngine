package com.example.pvpengine.player.dto;


import com.example.pvpengine.player.Player;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class PlayerResponse {
    private UUID id;
    private UUID gameId;
    private String externalPlayerId;
    private int rating;
    private String username;
    private String email;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastActiveAt;
    private boolean banned;
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
