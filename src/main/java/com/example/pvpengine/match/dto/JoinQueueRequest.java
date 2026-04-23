package com.example.pvpengine.match.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class JoinQueueRequest {

    @NotNull(message = "playerId is required")
    private UUID playerId;

    @Min(value = 0 , message = "Rating must be non-negativez")
    private int rating;
}
