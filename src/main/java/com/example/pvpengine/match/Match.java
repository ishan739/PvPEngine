package com.example.pvpengine.match;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "game_id", nullable = false, updatable = false)
    private UUID gameId;

    @Column(name = "player1_id" , nullable = false, updatable = false)
    private UUID player1Id;

    @Column(name = "player2_id" , nullable = false, updatable = false)
    private UUID player2Id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    @Builder.Default
    private MatchStatus status = MatchStatus.MATCH_FOUND;

    @Column(name = "game_session_id")
    private String gameSessionid;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
    }
}
