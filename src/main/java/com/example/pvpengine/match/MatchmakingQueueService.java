package com.example.pvpengine.match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchmakingQueueService {
    private static final String QUEUE_KEY_PREFIX= "queue:";

    private final RedisTemplate<String, String> redisTemplate;

    public void addToQueue(UUID gameId, UUID playerId , double rating) {
        String key  = queueKey(gameId);
        redisTemplate.opsForZSet().add(key, playerId.toString(), rating);
        log.info("Player {} added to queue for game {} with rating {}", playerId, gameId, rating);
    }

    public boolean removeFromQueue(UUID gameId, UUID playerId) {
        String key  = queueKey(gameId);
        Long removed = redisTemplate.opsForZSet().remove(key, playerId.toString());
        return removed != null && removed > 0;
    }


    public boolean isInQueue(UUID gameId, UUID playerId) {
        String key = queueKey(gameId);
        Double score = redisTemplate.opsForZSet().score(key, playerId.toString());
        return score != null;
    }

    public Set<ZSetOperations.TypedTuple<String>> getQueueWithScores(UUID gameId) {
        String key = queueKey(gameId);
        return redisTemplate.opsForZSet().rangeWithScores(key, 0L, -1);
    }

    public long getQueueSize(UUID gameId) {
        String key = queueKey(gameId);
        Long size = redisTemplate.opsForZSet().size(key);
        return size != null ? size : 0;
    }

    public boolean atomicRemovePair(UUID gameId, UUID player1Id, UUID player2Id) {
        String key = queueKey(gameId);
        Long removed1 = redisTemplate.opsForZSet().remove(key, player1Id.toString());
        if(removed1 == null || removed1 == 0){
            return false;
        }

        Long removed2 = redisTemplate.opsForZSet().remove(key, player2Id.toString());
        if(removed2 == null || removed2 == 0){
            Double player1Score = redisTemplate.opsForZSet().score(key, player1Id.toString());
            log.warn("Failed to remove player2 {} from queue , player1 {} may be lost", player2Id, player1Id);
            return false;
        }
        return true;
    }

    private String queueKey(UUID gameId) {
        return QUEUE_KEY_PREFIX + gameId.toString();
    }
}
