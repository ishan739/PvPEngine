package com.example.pvpengine.apiKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyCredential , UUID> {
    Optional<ApiKeyCredential> findByKeyPrefixAndActiveTrue(String key);

    List<ApiKeyCredential> findAllByGameId(UUID gameId);

    @Modifying
    @Query("UPDATE ApiKeyCredential a SET a.lastUsedAt = :now WHERE a.id = :id ")
    void updateLastUsedAt(UUID uuid, OffsetDateTime now);
}
