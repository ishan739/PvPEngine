package com.example.pvpengine.player;


import com.example.pvpengine.common.TenantContext;
import com.example.pvpengine.common.exception.PvpException;
import com.example.pvpengine.player.dto.CreatePlayerRequest;
import com.example.pvpengine.player.dto.PlayerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Override
    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request) {

        UUID gameId = TenantContext.requireGameId();
        if(playerRepository.existsByUsernameAndGameId(request.getUsername(), gameId)) {
            throw PvpException.conflict("Username is already taken: "+ request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && playerRepository.existsByEmailAndGameId(request.getEmail() , gameId)) {
            throw PvpException.conflict("Email is already taken: "+ request.getEmail());
        }

        if(request.getExternalPlayerId() != null && !request.getExternalPlayerId().isBlank()
            && playerRepository.existsByExternalPlayerIdAndGameId(request.getExternalPlayerId(), gameId)){
            throw PvpException.conflict("External Player Id already exists: "+ request.getExternalPlayerId());
        }


        Player player = Player.builder()
                .gameId(gameId)
                .username(request.getUsername())
                .email(request.getEmail())
                .externalPlayerId(request.getExternalPlayerId())
                .build();

        Player saved = playerRepository.save(player);
        log.info("Player created: id={}, gameId={} ", saved.getId() , gameId);

        return PlayerResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(UUID id) {
        return PlayerResponse.from(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getPlayerByUsername(String username) {
        UUID gameId = TenantContext.requireGameId();
        return playerRepository.findByUsernameAndGameId(username, gameId)
                .map(PlayerResponse::from)
                .orElseThrow(() -> PvpException.conflict("Username not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getPlayerByExternalId(String externalPlayerId) {
        UUID gameId = TenantContext.requireGameId();
        return playerRepository.findByExternalPlayerIdAndGameId(externalPlayerId, gameId)
                .map(PlayerResponse::from)
                .orElseThrow(() -> PvpException.conflict("Player not found with externalPlayerId: " + externalPlayerId));
    }

    @Override
    @Transactional
    public PlayerResponse banPlayer(UUID id, String reason) {
        Player player = findOrThrow(id);
        if(player.isBanned())
            throw PvpException.conflict("Player is already banned");
        player.setBanned(true);
        player.setBanReason(reason);
        return PlayerResponse.from(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponse unbanPlayer(UUID id) {
        Player player = findOrThrow(id);
        if (!player.isBanned())
            throw PvpException.conflict("Player is not banned");

        player.setBanned(false);
        player.setBanReason(null);
        return PlayerResponse.from(playerRepository.save(player));
    }

    @Override
    @Transactional
    public void updateLastActive(UUID id) {
        Player player = findOrThrow(id);
        player.setLastActiveAt(OffsetDateTime.now());
        playerRepository.save(player);
    }

    private Player findOrThrow(UUID id) {
        UUID gameId = TenantContext.requireGameId();
        return playerRepository.findByIdAndGameId(id , gameId)
                .orElseThrow(() -> PvpException.notFound("Player not found: " + id));
    }
}
