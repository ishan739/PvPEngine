package com.example.pvpengine.player;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity

@Table(
        name = "players",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_players_game_username" , columnNames = {"game_id","username"}),
                @UniqueConstraint(name = "uq_players_game_email", columnNames = {"game_id" , "email"}),
                @UniqueConstraint(name = "uq_players_game_external_id" , columnNames = {"game_id" , "external_player_id"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "game_id" , nullable = false, updatable = false)
    private UUID gameId;

    @Column(name = "external_player_id")
    private String externalPlayerId;

    @Column(nullable = false , unique = true , length = 50)
    private String username;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private int rating = 500;

    @Column(name = "created_at", nullable = false , updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_active_at", nullable = false)
    private OffsetDateTime lastActiveAt;

    @Column(name = "is_banned", nullable = false)
    @Builder.Default
    private boolean banned;

    @Column(name = "ban_reason")
    private String banReason;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.lastActiveAt = now;
    }
}
