package com.example.pvpengine.matchResult.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class MatchStartRequest {

    @NotNull(message = "startedAt is required")
    private OffsetDateTime startedAt;

    private String gameSessionid;
}
