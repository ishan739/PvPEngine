package com.example.pvpengine.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByIdAndGameId(UUID id , UUID gameId);
}
