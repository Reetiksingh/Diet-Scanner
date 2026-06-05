package com.nutrilens.user.api;

import com.nutrilens.user.api.dto.UserDtos.BodyMeasurementRequest;
import com.nutrilens.user.api.dto.UserDtos.GoalRequest;
import com.nutrilens.user.api.dto.UserDtos.GoalResponse;
import com.nutrilens.user.api.dto.UserDtos.ProfileResponse;
import com.nutrilens.user.api.dto.UserDtos.UpdateProfileRequest;
import com.nutrilens.user.application.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {
    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ProfileResponse me() {
        return userProfileService.currentProfile();
    }

    @PatchMapping("/profile")
    public ProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userProfileService.updateProfile(request);
    }

    @PatchMapping("/goals")
    public GoalResponse updateGoal(@Valid @RequestBody GoalRequest request) {
        return userProfileService.updateGoal(request);
    }

    @PostMapping("/body-measurements")
    public void bodyMeasurement(@Valid @RequestBody BodyMeasurementRequest request) {
        userProfileService.recordMeasurement(request);
    }
}

