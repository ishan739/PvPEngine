package com.example.pvpengine.kafka;

import com.example.pvpengine.player.Player;
import com.example.pvpengine.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchCompletedConsumer {

    private static final String CONSUMER_GROUP = "pvpengine-core";
    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:";
    private static final int K_FACTOR = 32;

    private final PlayerRepository playerRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(
            topics = MatchEventProducer.MATCH_COMPLETED_TOPIC,
            groupId = CONSUMER_GROUP
    )
    @Transactional
    public void consume(
            MatchCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.info("MatchCompletedConsumer received event for matchId={}", event.getMatchId());

        if (isAlreadyProcessed(event.getEventId())) {
            log.warn("Duplicate event skipped: eventId={}", event.getEventId());
            return;
        }
        markProcessed(event.getEventId(), topic);

        MatchCompletedEvent.PlayerResult p1Result = event.getPlayers().get(0);
        MatchCompletedEvent.PlayerResult p2Result = event.getPlayers().get(1);

        Optional<Player> player1Opt = playerRepository.findByIdAndGameId(
                p1Result.getPlayerId(), event.getGameId());
        Optional<Player> player2Opt = playerRepository.findByIdAndGameId(
                p2Result.getPlayerId(), event.getGameId());

        if (player1Opt.isEmpty() || player2Opt.isEmpty()) {
            log.error("Players not found for matchId={}", event.getMatchId());
            return;
        }

        Player player1 = player1Opt.get();
        Player player2 = player2Opt.get();

        double actual1 = event.isDraw() ? 0.5 : (p1Result.isWinner() ? 1.0 : 0.0);
        double actual2 = event.isDraw() ? 0.5 : (p2Result.isWinner() ? 1.0 : 0.0);

        double expected1 = expectedScore(player1.getRating(), player2.getRating());
        double expected2 = expectedScore(player2.getRating(), player1.getRating());

        int newRating1 = Math.max(100, (int) Math.round(player1.getRating() + K_FACTOR * (actual1 - expected1)));
        int newRating2 = Math.max(100, (int) Math.round(player2.getRating() + K_FACTOR * (actual2 - expected2)));

        log.info("ELO update: matchId={}, player1 {}→{}, player2 {}→{}",
                event.getMatchId(),
                player1.getRating(), newRating1,
                player2.getRating(), newRating2);

        player1.setRating(newRating1);
        player2.setRating(newRating2);
        playerRepository.save(player1);
        playerRepository.save(player2);

        String leaderboardKey = LEADERBOARD_KEY_PREFIX + event.getGameId();
        redisTemplate.opsForZSet().add(leaderboardKey, player1.getId().toString(), newRating1);
        redisTemplate.opsForZSet().add(leaderboardKey, player2.getId().toString(), newRating2);

        log.info("Leaderboard updated: gameId={}, player1={} rating={}, player2={} rating={}",
                event.getGameId(),
                player1.getId(), newRating1,
                player2.getId(), newRating2);
    }

    private double expectedScore(int ratingA, int ratingB) {
        return 1.0 / (1.0 + Math.pow(10.0, (ratingB - ratingA) / 400.0));
    }

    private boolean isAlreadyProcessed(String eventId) {
        return processedEventRepository.existsByConsumerGroupAndEventId(CONSUMER_GROUP, eventId);
    }

    private void markProcessed(String eventId, String topic) {
        try {
            ProcessedEvent pe = ProcessedEvent.builder()
                    .consumerGroup(CONSUMER_GROUP)
                    .eventId(eventId)
                    .topic(topic)
                    .build();
            processedEventRepository.save(pe);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEventException("Already processed: " + eventId);
        }
    }
}