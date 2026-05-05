package com.example.pvpengine.matchResult;

import com.example.pvpengine.common.TenantContext;
import com.example.pvpengine.common.exception.PvpException;
import com.example.pvpengine.kafka.MatchCompletedEvent;
import com.example.pvpengine.kafka.MatchEventProducer;
import com.example.pvpengine.match.Match;
import com.example.pvpengine.match.MatchRepository;
import com.example.pvpengine.match.MatchStatus;
import com.example.pvpengine.matchResult.dto.MatchCompleteRequest;
import com.example.pvpengine.matchResult.dto.MatchStartRequest;
import com.example.pvpengine.player.Player;
import com.example.pvpengine.player.PlayerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchService{

    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;
    private final PlayerRepository playerRepository;
    private final MatchEventProducer matchEventProducer;
    private ObjectMapper objectMapper ;

    @Override
    @Transactional
    public MatchResponse startMatch(UUID matchId, MatchStartRequest request) {
        UUID gameId = TenantContext.requireGameId();
        Match match = findOrThrow(matchId, gameId);

        if(match.getStatus() != MatchStatus.MATCH_FOUND){
            throw PvpException.conflict("Match cannot be started — current status: " + match.getStatus());
        }

        match.setStatus(MatchStatus.IN_PROGRESS);
        match.setStartedAt(request.getStartedAt());
        match.setGameSessionid(request.getGameSessionid());

        Match saved = matchRepository.save(match);
        log.info("Match started: id={}, gameId={}", matchId, gameId);
        return MatchResponse.from(saved);
    }

    @Override
    @Transactional
    public MatchResponse completeMatch(UUID matchId, MatchCompleteRequest request) {
        UUID gameId = TenantContext.requireGameId();
        Match match = findOrThrow(matchId, gameId);

        if(match.getStatus() != MatchStatus.IN_PROGRESS){
            throw PvpException.conflict("Match cannot be started — current status: " + match.getStatus());
        }



        if (!request.isDraw()) {
            long winnerCount = request.getResults().stream()
                    .filter(MatchCompleteRequest.PlayersResultRequest::isWinner)
                    .count();
            if (winnerCount != 1) {
                throw PvpException.badRequest("Exactly one winner required when not a draw");
            }
        }

        MatchCompleteRequest.PlayersResultRequest winnerResult = request.getResults().stream()
                .filter(MatchCompleteRequest.PlayersResultRequest::isWinner)
                .findFirst()
                .orElseThrow();

        Player player1 = playerRepository.findByIdAndGameId(match.getPlayer1Id(), gameId).
                orElseThrow(() -> PvpException.conflict("Player 1 not found"));
        Player player2 = playerRepository.findByIdAndGameId(match.getPlayer2Id(), gameId).
                orElseThrow(() -> PvpException.conflict("Player 2 not found"));

        int player1RatingBefore = player1.getRating();
        int player2RatingBefore = player2.getRating();


        int player1Score = getScore(request , match.getPlayer1Id());
        int player2Score = getScore(request , match.getPlayer2Id());

        String rawPayload = serializePayload(request);
        MatchResult result = MatchResult.builder()
                .matchId(matchId)
                .player1Score(player1Score)
                .player2Score(player2Score)
                .winnerPlayerId(winnerResult.getPlayerId())
                .rawPayload(rawPayload)
                .build();
        matchResultRepository.save(result);

        match.setStatus(MatchStatus.COMPLETED);
        match.setCompletedAt(request.getCompletedAt());
        Match saved = matchRepository.save(match);

        //kafka event
        MatchCompletedEvent event = MatchCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .version("v1")
                .matchId(matchId)
                .gameId(gameId)
                .winnerPlayerId(winnerResult.getPlayerId())
                .occurredAt(OffsetDateTime.now())
                .players(List.of(
                        MatchCompletedEvent.PlayerResult.builder()
                                .playerId(match.getPlayer1Id())
                                .score(player1Score)
                                .winner(match.getPlayer1Id().equals(winnerResult.getPlayerId()))
                                .ratingBefore(player1RatingBefore)
                                .build(),
                        MatchCompletedEvent.PlayerResult.builder()
                                .playerId(match.getPlayer2Id())
                                .score(player2Score)
                                .winner(match.getPlayer2Id().equals(winnerResult.getPlayerId()))
                                .ratingBefore(player2RatingBefore)
                                .build()
                ))
                .build();

        matchEventProducer.publishMatchCompleted(event);
        log.info("Match completed: id={}, winner={}", matchId, winnerResult.getPlayerId());

        return  MatchResponse.from(saved);

    }

    private Match findOrThrow(UUID matchId, UUID gameId) {
        return matchRepository.findByIdAndGameId(matchId, gameId)
                .orElseThrow(() -> PvpException.notFound("Match not found: " + matchId));
    }

    private int getScore(MatchCompleteRequest request, UUID playerId) {
        return request.getResults().stream()
                .filter(r -> r.getPlayerId().equals(playerId))
                .findFirst()
                .map(MatchCompleteRequest.PlayersResultRequest::getScore)
                .orElse(0);
    }

    private String serializePayload(MatchCompleteRequest request) {
        try{
            return objectMapper.writeValueAsString(request);
        } catch (Exception e){
            log.warn("Failed to serialize match complete payload", e);
            return null;
        }
    }
}
