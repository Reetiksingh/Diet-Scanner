package com.nutrilens.user.application;

import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import com.nutrilens.user.api.dto.UserDtos.BodyMeasurementRequest;
import com.nutrilens.user.api.dto.UserDtos.GoalRequest;
import com.nutrilens.user.api.dto.UserDtos.GoalResponse;
import com.nutrilens.user.api.dto.UserDtos.ProfileResponse;
import com.nutrilens.user.api.dto.UserDtos.UpdateProfileRequest;
import com.nutrilens.user.domain.BodyMeasurement;
import com.nutrilens.user.domain.UserAccount;
import com.nutrilens.user.domain.UserGoal;
import com.nutrilens.user.domain.UserProfile;
import com.nutrilens.user.infrastructure.BodyMeasurementRepository;
import com.nutrilens.user.infrastructure.UserAccountRepository;
import com.nutrilens.user.infrastructure.UserGoalRepository;
import com.nutrilens.user.infrastructure.UserProfileRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserGoalRepository userGoalRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserProfileService(UserAccountRepository userAccountRepository,
                              UserProfileRepository userProfileRepository,
                              UserGoalRepository userGoalRepository,
                              BodyMeasurementRepository bodyMeasurementRepository,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.userGoalRepository = userGoalRepository;
        this.bodyMeasurementRepository = bodyMeasurementRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public ProfileResponse currentProfile() {
        UUID userId = SecuritySupport.currentUserId();
        UserAccount account = userAccountRepository.findById(userId)
                .orElseGet(() -> new UserAccount(userId, "developer@nutrilens.local", "NutriLens Developer", "IN"));
        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> new UserProfile(userId));
        GoalResponse goal = userGoalRepository.findFirstByUserIdAndActiveTrueOrderByCreatedAtDesc(userId).map(this::toGoalResponse).orElse(null);
        return toProfileResponse(account, profile, goal);
    }

    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        UUID userId = SecuritySupport.currentUserId();
        UserAccount account = userAccountRepository.findById(userId)
                .orElseGet(() -> new UserAccount(userId, "developer@nutrilens.local", request.displayName(), request.countryCode()));
        account.updateProfile(request.displayName(), request.avatarUrl(), request.countryCode(), request.timezone());
        userAccountRepository.save(account);
        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> new UserProfile(userId));
        profile.update(request.age(), request.heightCm(), request.weightKg(), request.gender(), request.activityLevel(), request.dietaryPreference());
        userProfileRepository.save(profile);
        kafkaTemplate.send(KafkaTopics.USER_PROFILE_UPDATED, userId.toString(), EventEnvelope.of(
                "UserProfileUpdatedEvent",
                "user-service",
                UUID.randomUUID().toString(),
                new Events.UserProfileUpdatedEvent(userId, request.countryCode(), request.activityLevel(), request.dietaryPreference())
        ));
        return toProfileResponse(account, profile, userGoalRepository.findFirstByUserIdAndActiveTrueOrderByCreatedAtDesc(userId).map(this::toGoalResponse).orElse(null));
    }

    @Transactional
    public GoalResponse updateGoal(GoalRequest request) {
        UUID userId = SecuritySupport.currentUserId();
        userGoalRepository.findFirstByUserIdAndActiveTrueOrderByCreatedAtDesc(userId).ifPresent(UserGoal::deactivate);
        UserGoal goal = userGoalRepository.save(new UserGoal(
                userId,
                request.goalType(),
                request.targetCalories(),
                request.proteinTargetG(),
                request.carbsTargetG(),
                request.fatsTargetG(),
                request.waterTargetMl()
        ));
        kafkaTemplate.send(KafkaTopics.USER_GOAL_UPDATED, userId.toString(), EventEnvelope.of(
                "UserGoalUpdatedEvent",
                "user-service",
                UUID.randomUUID().toString(),
                new Events.UserGoalUpdatedEvent(userId, request.goalType(), request.targetCalories(),
                        toDouble(request.proteinTargetG()), toDouble(request.carbsTargetG()), toDouble(request.fatsTargetG()), request.waterTargetMl())
        ));
        return toGoalResponse(goal);
    }

    @Transactional
    public void recordMeasurement(BodyMeasurementRequest request) {
        UUID userId = SecuritySupport.currentUserId();
        bodyMeasurementRepository.save(new BodyMeasurement(userId, request.measuredOn(), request.weightKg(), request.bodyFatPercent()));
    }

    private ProfileResponse toProfileResponse(UserAccount account, UserProfile profile, GoalResponse goal) {
        return new ProfileResponse(account.getId(), account.getEmail(), account.getDisplayName(), account.getAvatarUrl(),
                account.getCountryCode(), account.getTimezone(), profile.getAge(), profile.getHeightCm(), profile.getWeightKg(),
                profile.getGender(), profile.getActivityLevel(), profile.getDietaryPreference(), goal);
    }

    private GoalResponse toGoalResponse(UserGoal goal) {
        return new GoalResponse(goal.getId(), goal.getGoalType(), goal.getTargetCalories(),
                goal.getProteinTargetG(), goal.getCarbsTargetG(), goal.getFatsTargetG(), goal.getWaterTargetMl());
    }

    private double toDouble(BigDecimal value) {
        return value == null ? 0 : value.doubleValue();
    }
}

