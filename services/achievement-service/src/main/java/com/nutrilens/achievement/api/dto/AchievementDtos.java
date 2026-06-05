package com.nutrilens.achievement.api.dto;

import java.util.List;
import java.util.UUID;

public final class AchievementDtos {
    private AchievementDtos() {
    }

    public record AchievementResponse(UUID id, String code, String name, String description, boolean unlocked) {
    }

    public record AchievementList(List<AchievementResponse> achievements) {
    }
}

