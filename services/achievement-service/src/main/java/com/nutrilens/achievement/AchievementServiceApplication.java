package com.nutrilens.achievement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nutrilens.achievement", "com.nutrilens.common.security", "com.nutrilens.common.observability"})
public class AchievementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AchievementServiceApplication.class, args);
    }
}

