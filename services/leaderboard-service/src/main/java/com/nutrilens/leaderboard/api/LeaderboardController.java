package com.nutrilens.leaderboard.api;

import com.nutrilens.leaderboard.api.dto.LeaderboardDtos.LeaderboardResponse;
import com.nutrilens.leaderboard.api.dto.LeaderboardDtos.RankResponse;
import com.nutrilens.leaderboard.application.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leaderboards")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/global")
    public LeaderboardResponse global(@RequestParam String metric, @RequestParam String period, @RequestParam(defaultValue = "50") int limit) {
        return leaderboardService.leaderboard("global", null, metric, period, limit);
    }

    @GetMapping("/country/{countryCode}")
    public LeaderboardResponse country(@PathVariable String countryCode, @RequestParam String metric, @RequestParam String period, @RequestParam(defaultValue = "50") int limit) {
        return leaderboardService.leaderboard("country", countryCode, metric, period, limit);
    }

    @GetMapping("/me/rank")
    public RankResponse rank(@RequestParam String metric, @RequestParam String period) {
        return leaderboardService.myRank(metric, period);
    }
}

