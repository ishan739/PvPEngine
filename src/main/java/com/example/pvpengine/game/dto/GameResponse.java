package com.example.pvpengine.game.dto;

import com.example.pvpengine.game.Game;
import com.example.pvpengine.game.GameStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class GameResponse {
    private UUID id;
    private String name;
    private String code;
    private String contactEmail;
    private String webhookUrl;
    private GameStatus gameStatus;
    private OffsetDateTime createdAt;

    public static GameResponse from(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .code(game.getCode())
                .name(game.getName())
                .gameStatus(game.getStatus())
                .contactEmail(game.getContactEmail())
                .createdAt(game.getCreatedAt())
                .webhookUrl(game.getWebhookUrl())
                .build();
    }
}
