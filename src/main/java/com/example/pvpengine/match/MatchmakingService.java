package com.example.pvpengine.match;

import com.example.pvpengine.match.dto.JoinQueueRequest;
import com.example.pvpengine.match.dto.LeaveQueueRequest;

public interface MatchmakingService {
    void joinQueue(JoinQueueRequest request);
    void leaveQueue(LeaveQueueRequest request);
}
