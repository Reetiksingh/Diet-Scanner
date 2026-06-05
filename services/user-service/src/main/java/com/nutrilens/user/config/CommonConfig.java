package com.nutrilens.user.config;

import com.nutrilens.common.observability.CorrelationIdFilter;
import com.nutrilens.common.events.KafkaTopics;
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
    KafkaAdmin.NewTopics userTopics() {
        return new KafkaAdmin.NewTopics(topic(KafkaTopics.USER_PROFILE_UPDATED), topic(KafkaTopics.USER_GOAL_UPDATED));
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

