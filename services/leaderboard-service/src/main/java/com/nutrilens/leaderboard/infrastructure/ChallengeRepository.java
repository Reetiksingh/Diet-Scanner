package com.nutrilens.leaderboard.infrastructure;

import com.nutrilens.leaderboard.domain.Challenge;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByStatus(String status);
}

