package com.nutrilens.achievement.api;

import com.nutrilens.achievement.api.dto.AchievementDtos.AchievementList;
import com.nutrilens.achievement.application.AchievementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public AchievementList all() {
        return achievementService.all();
    }

    @GetMapping("/me")
    public AchievementList mine() {
        return achievementService.all();
    }

    @GetMapping("/me/progress")
    public AchievementList progress() {
        return achievementService.all();
    }
}

