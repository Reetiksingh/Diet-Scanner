package com.nutrilens.achievement.infrastructure;

import com.nutrilens.achievement.domain.UserAchievement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    Optional<UserAchievement> findByUserIdAndAchievementId(UUID userId, UUID achievementId);
    List<UserAchievement> findByUserId(UUID userId);
}

