package com.example.pvpengine.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game , UUID> {
    boolean existsByCode(String code);
    Optional<Game> findByCode(String code);
    List<Game> findAllByStatus(GameStatus status);
}
