package com.nutrilens.auth.config;

import com.nutrilens.common.events.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {
    @Bean
    KafkaAdmin.NewTopics authTopics() {
        return new KafkaAdmin.NewTopics(
                topic(KafkaTopics.USER_REGISTERED),
                topic(KafkaTopics.USER_LOGGED_IN),
                topic(KafkaTopics.SESSION_REVOKED)
        );
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
