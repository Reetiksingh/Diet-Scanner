package com.nutrilens.leaderboard.infrastructure;

import com.nutrilens.leaderboard.domain.ChallengeParticipant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, UUID> {
}

