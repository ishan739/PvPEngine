package com.example.pvpengine.match;

import com.example.pvpengine.common.TenantContext;
import com.example.pvpengine.common.exception.PvpException;
import com.example.pvpengine.match.dto.JoinQueueRequest;
import com.example.pvpengine.match.dto.LeaveQueueRequest;
import com.example.pvpengine.player.Player;
import com.example.pvpengine.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchmakingServiceImpl implements MatchmakingService{

    private final MatchmakingQueueService queueService;
    private final PlayerRepository playerRepository;

    @Override
    public void joinQueue(JoinQueueRequest request) {
        UUID gameId = TenantContext.requireGameId();
        UUID playerId = request.getPlayerId();

        Player player = playerRepository.findByIdAndGameId(playerId , gameId)
                .orElseThrow(()-> PvpException.notFound("Player not found" + playerId));

        if(player.isBanned()){
            throw PvpException.forbidden("Banned players cannot join matchmaking");
        }

        if(queueService.isInQueue(gameId, playerId)){
            throw PvpException.conflict("Player is already in matchmaking queue");
        }

        queueService.addToQueue(gameId , playerId , player.getRating());
        log.info("Player {} joined queue for game {} with rating {}", playerId, gameId, player.getRating());
    }

    @Override
    public void leaveQueue(LeaveQueueRequest request) {
        UUID gameId = TenantContext.requireGameId();
        UUID playerId = request.getPlayerId();

        boolean removed = queueService.removeFromQueue(gameId , playerId);
        if(!removed){
            throw PvpException.notFound("Player is not in matchmaking queue");
        }

        log.info("Player {} left queue for game {}", playerId, gameId);
    }
}
