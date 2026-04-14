package com.example.pvpengine.game;

import com.example.pvpengine.common.exception.PvpException;
import com.example.pvpengine.game.dto.CreateGameRequest;
import com.example.pvpengine.game.dto.GameResponse;
import com.example.pvpengine.game.dto.UpdateWebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    @Transactional
    @Override
    public GameResponse createGame(CreateGameRequest request) {
        if (gameRepository.existsByCode(request.getCode())) {
            throw PvpException.conflict("Game code already exists: " + request.getCode());
        }

        Game game = Game.builder()
                .name(request.getName())
                .code(request.getCode())
                .contactEmail(request.getContactEmail())
                .webhookUrl(request.getWebhookUrl())
                .build();

        Game saved = gameRepository.save(game);
        log.info("Game created: id={}, code={}", saved.getId(), saved.getCode());
        return GameResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGameById(UUID id) {
        return GameResponse.from(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGameByCode(String code) {
        return gameRepository.findByCode(code)
                .map(GameResponse::from)
                .orElseThrow(() -> PvpException.notFound("Game code not found: " + code));
    }

    @Override
    @Transactional
    public GameResponse updateWebhookUrl(UUID id, UpdateWebhookRequest request) {
        Game game = findOrThrow(id);
        game.setWebhookUrl(request.getWebhookUrl());
        return GameResponse.from(gameRepository.save(game));
    }

    @Override
    @Transactional
    public GameResponse suspendGame(UUID id) {
        Game game = findOrThrow(id);
        if(game.getStatus() == GameStatus.SUSPENDED) {
            throw PvpException.conflict("Game is already suspended");
        }
        game.setStatus(GameStatus.SUSPENDED);
        return GameResponse.from(gameRepository.save(game));
    }

    @Transactional
    @Override
    public GameResponse activateGame(UUID id) {
        Game game = findOrThrow(id);
        if(game.getStatus() == GameStatus.ACTIVE) {
            throw PvpException.conflict("Game is already active");
        }
        game.setStatus(GameStatus.ACTIVE);
        return GameResponse.from(gameRepository.save(game));
    }

    public Game findOrThrow(UUID id) {
        return gameRepository.findById(id).orElseThrow(() -> PvpException.notFound("Game not found: " + id));
    }
}
