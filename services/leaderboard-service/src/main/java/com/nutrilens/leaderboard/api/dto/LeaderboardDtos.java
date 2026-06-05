package com.nutrilens.leaderboard.api.dto;

import java.util.List;
import java.util.UUID;

public final class LeaderboardDtos {
    private LeaderboardDtos() {
    }

    public record RankingItem(long rank, UUID userId, String displayName, double score, String countryCode) {
    }

    public record LeaderboardResponse(String metric, String period, List<RankingItem> items) {
    }

    public record RankResponse(String key, UUID userId, long rank, double score) {
    }
}

