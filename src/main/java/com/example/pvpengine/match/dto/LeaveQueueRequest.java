package com.example.pvpengine.match.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class LeaveQueueRequest {

    @NotNull(message = "playerId is required")
    private UUID playerId;
}
