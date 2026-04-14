package com.example.pvpengine.game;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true , length = 100)
    private String name;

    @Column(nullable = false, unique = true , length = 50)
    private String code;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "webhook_url" , length = 500)
    private String webhookUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    @Builder.Default
    private GameStatus status = GameStatus.ACTIVE;

    @Column(nullable = false , updatable = false , name = "created_at")
    private OffsetDateTime createdAt;

    @Column(nullable = false , name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist(){
        OffsetDateTime now = OffsetDateTime.now();
        this.updatedAt = now;
        this.createdAt = now;
    }

    @PreUpdate
    void preUpdate(){
        this.updatedAt = OffsetDateTime.now();
    }
}
