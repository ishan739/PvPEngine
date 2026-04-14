package com.example.pvpengine.apiKey;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.actuate.endpoint.jmx.JmxEndpointsSupplier;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "game_id" , nullable = false)
    private UUID gameId;

    @Column(name = "key_prefix" , nullable = false , unique = true , length = 30)
    private String keyPrefix;

    @Column(name = "keyHash", nullable = false)
    private String keyHash;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }


    public boolean isExpired() {
        return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
    }

}
