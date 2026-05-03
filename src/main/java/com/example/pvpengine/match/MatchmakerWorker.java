package com.example.pvpengine.match;

import com.example.pvpengine.game.Game;
import com.example.pvpengine.game.GameRepository;
import com.example.pvpengine.game.GameStatus;
import com.example.pvpengine.player.Player;
import com.example.pvpengine.player.PlayerRepository;
import com.example.pvpengine.webhook.WebhookDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchmakerWorker {
    private final MatchmakingQueueService queueService;
    private final GameRepository gameRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final WebhookDeliveryService webhookDeliveryService;

    @Scheduled(fixedDelay= 3000)
    public void runMatchMaking() {
        List<Game> activeGames = gameRepository.findAllByStatus(GameStatus.ACTIVE);

        for (Game game : activeGames) {
            try{
                processQueue(game);
            } catch (Exception e) {
                log.error("Error processing matchmaking queue for game {}: {}", game.getId(), e.getMessage(), e);
            }
        }
    }

    private void processQueue(Game game) {
        UUID gameId = game.getId();
        long queueSize = queueService.getQueueSize(gameId);

        if(queueSize<2){
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> queueEntries = queueService.getQueueWithScores(gameId);
        if(queueEntries == null || queueEntries.size() < 2){
            return;
        }

        List<ZSetOperations.TypedTuple<String>> players = new ArrayList<>(queueEntries);

        for (int i = 0; i < players.size(); i += 2) {
            ZSetOperations.TypedTuple<String> p1Entry = players.get(i);
            ZSetOperations.TypedTuple<String> p2Entry = players.get(i+1);

            if(p1Entry.getValue() == null || p2Entry.getValue() == null) continue;
            if(p1Entry.getScore() == null || p2Entry.getScore() == null) continue;

            UUID player1Id = UUID.fromString(p1Entry.getValue());
            UUID player2Id = UUID.fromString(p2Entry.getValue());
            double ratingDiff = Math.abs(p1Entry.getScore() - p2Entry.getScore());

            log.debug("Attempting to pair player {} (rating {}) with player {} (rating {}) for game {} with name {}",
                    player1Id, p1Entry.getScore(), player2Id, p2Entry.getScore(), gameId, game.getName());

            boolean paired = queueService.atomicRemovePair(gameId, player1Id, player2Id);
            if(!paired){
                log.warn("Failed to atomically remove pair for game {} — skipping", gameId);
                continue;
            }


            createMatch(gameId , player1Id , player2Id , game);
        }
    }

    @Transactional
    protected void createMatch(UUID gameId, UUID player1Id, UUID player2Id, Game game) {
        Optional<Player> p1Opt = playerRepository.findByIdAndGameId(player1Id, gameId);
        Optional<Player> p2Opt = playerRepository.findByIdAndGameId(player2Id, gameId);

        if (p1Opt.isEmpty() || p2Opt.isEmpty()) {
            log.error("Player not found during match creation — gameId={}, p1={}, p2={}", gameId, player1Id, player2Id);
            return;
        }

        Player player1 = p1Opt.get();
        Player player2 = p2Opt.get();

        Match match = Match.builder()
                .gameId(gameId)
                .player1Id(player1Id)
                .player2Id(player2Id)
                .status(MatchStatus.MATCH_FOUND)
                .build();

        Match saved = matchRepository.save(match);
        log.info("Match created: id={}, game={}, player1={}, player2={}",
                saved.getId(), gameId, player1Id, player2Id);

        if(game.getWebhookUrl() != null && !game.getWebhookUrl().isBlank()){
            webhookDeliveryService.scheduleMatchFoundWebhook(match, player1 , player2, game.getWebhookUrl());
        } else {
            log.warn("Game {} has no webhookUrl configured — skipping webhook delivery", gameId);
        }
    }

}
