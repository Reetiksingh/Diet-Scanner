package com.nutrilens.leaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nutrilens.leaderboard", "com.nutrilens.common.security", "com.nutrilens.common.observability"})
public class LeaderboardServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaderboardServiceApplication.class, args);
    }
}

