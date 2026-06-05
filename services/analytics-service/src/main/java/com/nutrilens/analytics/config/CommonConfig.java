package com.nutrilens.analytics.config;

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
    KafkaAdmin.NewTopics analyticsTopics() {
        return new KafkaAdmin.NewTopics(
                topic(KafkaTopics.DAILY_COMPUTED),
                topic(KafkaTopics.WEEKLY_REPORT_GENERATED),
                topic(KafkaTopics.INSIGHT_GENERATED),
                topic(KafkaTopics.STREAK_UPDATED)
        );
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

