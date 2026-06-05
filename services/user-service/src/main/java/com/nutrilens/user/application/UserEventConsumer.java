package com.nutrilens.user.application;

import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.user.domain.UserAccount;
import com.nutrilens.user.domain.UserProfile;
import com.nutrilens.user.infrastructure.UserAccountRepository;
import com.nutrilens.user.infrastructure.UserProfileRepository;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserEventConsumer {
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;

    public UserEventConsumer(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED, groupId = "user-service")
    @Transactional
    public void onUserRegistered(EventEnvelope<?> envelope) {
        Object payload = envelope.payload();
        if (payload instanceof LinkedHashMap<?, ?> map) {
            UUID userId = UUID.fromString(String.valueOf(map.get("userId")));
            String email = String.valueOf(map.get("email"));
            String displayName = String.valueOf(map.get("displayName"));
            String countryCode = String.valueOf(map.get("countryCode"));
            userAccountRepository.findById(userId).orElseGet(() -> userAccountRepository.save(new UserAccount(userId, email, displayName, countryCode)));
            userProfileRepository.findById(userId).orElseGet(() -> userProfileRepository.save(new UserProfile(userId)));
        } else if (payload instanceof Events.UserRegisteredEvent event) {
            userAccountRepository.findById(event.userId()).orElseGet(() -> userAccountRepository.save(new UserAccount(event.userId(), event.email(), event.displayName(), event.countryCode())));
            userProfileRepository.findById(event.userId()).orElseGet(() -> userProfileRepository.save(new UserProfile(event.userId())));
        }
    }
}

