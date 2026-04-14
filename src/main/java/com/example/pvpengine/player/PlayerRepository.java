package com.example.pvpengine.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository  extends JpaRepository<Player, UUID> {
    Optional<Player> findByIdAndGameId(UUID id, UUID gameId);
    Optional<Player> findByUsernameAndGameId(String username , UUID gameId);
    Optional<Player> findByExternalPlayerIdAndGameId(String externalPlayerId, UUID gameId);
    boolean existsByUsernameAndGameId(String username , UUID gameId);
    boolean existsByEmailAndGameId(String email , UUID gameId);
    boolean existsByExternalPlayerIdAndGameId(String externalPlayerId, UUID gameId);
}
