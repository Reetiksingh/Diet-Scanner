package com.nutrilens.user.infrastructure;

import com.nutrilens.user.domain.UserGoal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGoalRepository extends JpaRepository<UserGoal, UUID> {
    Optional<UserGoal> findFirstByUserIdAndActiveTrueOrderByCreatedAtDesc(UUID userId);
}

