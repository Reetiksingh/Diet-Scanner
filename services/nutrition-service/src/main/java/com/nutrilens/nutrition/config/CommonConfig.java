package com.nutrilens.nutrition.config;

import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.observability.CorrelationIdFilter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class CommonConfig {
    @Bean
    CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }

    @Bean
    KafkaAdmin.NewTopics nutritionTopics() {
        return new KafkaAdmin.NewTopics(
                topic(KafkaTopics.MEAL_LOGGED),
                topic(KafkaTopics.MEAL_UPDATED),
                topic(KafkaTopics.MEAL_DELETED),
                topic(KafkaTopics.FOOD_CREATED),
                topic(KafkaTopics.NUTRITION_GOAL_REACHED)
        );
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

