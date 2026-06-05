package com.nutrilens.nutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nutrilens.nutrition", "com.nutrilens.common.security", "com.nutrilens.common.observability"})
public class NutritionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NutritionServiceApplication.class, args);
    }
}

