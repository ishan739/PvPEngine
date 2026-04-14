package com.example.pvpengine.player;


import com.example.pvpengine.common.exception.PvpException;
import com.example.pvpengine.player.dto.CreatePlayerRequest;
import com.example.pvpengine.player.dto.PlayerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Override
    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        if(playerRepository.existsByUsername(request.getUsername()))
            throw PvpException.conflict("Username already taken: " + request.getUsername());

        if(playerRepository.existsByEmail(request.getEmail()))
            throw PvpException.conflict("Email already taken: " + request.getEmail());

        Player player = Player.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();

        return PlayerResponse.from(playerRepository.save(player));
    }

    @Override
    public PlayerResponse getPlayerById(UUID id) {
        return PlayerResponse.from(findOrThrow(id));
    }

    @Override
    public PlayerResponse getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username)
                .map(PlayerResponse::from)
                .orElseThrow(() -> PvpException.conflict("Username not found: " + username));
    }

    @Override
    public PlayerResponse banPlayer(UUID id, String reason) {
        Player player = findOrThrow(id);
        if(player.isBanned())
            throw PvpException.conflict("Player is already banned");
        player.setBanned(true);
        player.setBanReason(reason);
        return PlayerResponse.from(playerRepository.save(player));
    }

    @Override
    public PlayerResponse unbanPlayer(UUID id) {
        Player player = findOrThrow(id);
        if (!player.isBanned())
            throw PvpException.conflict("Player is not banned");

        player.setBanned(false);
        player.setBanReason(null);
        return PlayerResponse.from(playerRepository.save(player));
    }

    @Override
    public void updateLastActive(UUID id) {
        Player player = findOrThrow(id);
        player.setLastActiveAt(OffsetDateTime.now());
        playerRepository.save(player);
    }

    private Player findOrThrow(UUID id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> PvpException.notFound("Player not found: " + id));
    }
}
