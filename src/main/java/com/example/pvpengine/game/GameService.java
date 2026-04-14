package com.example.pvpengine.game;

import com.example.pvpengine.game.dto.CreateGameRequest;
import com.example.pvpengine.game.dto.GameResponse;
import com.example.pvpengine.game.dto.UpdateWebhookRequest;

import java.util.UUID;

public interface GameService {
    GameResponse createGame(CreateGameRequest request);
    GameResponse getGameById(UUID id);
    GameResponse getGameByCode(String code);
    GameResponse updateWebhookUrl(UUID id, UpdateWebhookRequest request);
    GameResponse suspendGame(UUID id);
    GameResponse activateGame(UUID id);
}
