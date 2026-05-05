package com.example.pvpengine.matchResult;

import com.example.pvpengine.matchResult.dto.MatchCompleteRequest;
import com.example.pvpengine.matchResult.dto.MatchStartRequest;

import java.util.UUID;

public interface MatchService {

    MatchResponse startMatch(UUID matchId , MatchStartRequest request);
    MatchResponse completeMatch(UUID matchId , MatchCompleteRequest request);
}
