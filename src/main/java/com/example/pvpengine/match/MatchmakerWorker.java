package com.example.pvpengine.match;

import com.example.pvpengine.game.Game;
import com.example.pvpengine.game.GameRepository;
import com.example.pvpengine.game.GameStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchmakerWorker {
    private final MatchmakingQueueService queueService;
    private final GameRepository gameRepository;
    private final MatchRepository matchRepository;

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

            log.debug("Attempting to pair player {} (rating {}) with player {} (rating {}) for game {}",
                    player1Id, p1Entry.getScore(), player2Id, p2Entry.getScore(), gameId);

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
        Match match = Match.builder()
                .gameId(gameId)
                .player1Id(player1Id)
                .player2Id(player2Id)
                .status(MatchStatus.MATCH_FOUND)
                .build();

        Match saved = matchRepository.save(match);
        log.info("Match created: id={}, game={}, player1={}, player2={}",
                saved.getId(), gameId, player1Id, player2Id);

//      Later we will trigger webhook to game.getWebhookUrl()
    }

}
