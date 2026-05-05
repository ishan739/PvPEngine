package com.example.pvpengine.matchResult;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_results")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "match_id", nullable = false, unique = true)
    private UUID matchId;


    @Column(name = "winner_player_id", nullable = false)
    private UUID winnerPlayerId;

    @Column(name = "player1_score" , nullable = false)
    private int player1Score;

    @Column(name = "player2_score" , nullable = false)
    private int player2Score;

    @Column(name = "raw_payload" , columnDefinition = "TEXT")
    private String rawPayload;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}
