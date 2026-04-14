package com.example.pvpengine.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository  extends JpaRepository<Player, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Player> findByUsername(String username);
}
