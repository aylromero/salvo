package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.entities.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
