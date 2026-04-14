package com.example.pvpengine.player;


import com.example.pvpengine.player.dto.CreatePlayerRequest;
import com.example.pvpengine.player.dto.PlayerResponse;

import java.util.UUID;

public interface PlayerService {
    PlayerResponse createPlayer(CreatePlayerRequest request);
    PlayerResponse getPlayerById(UUID id);
    PlayerResponse getPlayerByUsername(String username);
    PlayerResponse banPlayer(UUID id , String reason);
    PlayerResponse unbanPlayer(UUID id);
    void updateLastActive(UUID id);
}
