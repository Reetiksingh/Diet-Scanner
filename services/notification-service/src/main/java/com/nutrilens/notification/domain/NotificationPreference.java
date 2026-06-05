package com.nutrilens.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences", schema = "notifications")
public class NotificationPreference {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "achievement_notifications")
    private boolean achievementNotifications;
    @Column(name = "streak_reminders")
    private boolean streakReminders;
    @Column(name = "weekly_reports")
    private boolean weeklyReports;
    @Column(name = "challenge_updates")
    private boolean challengeUpdates;

    protected NotificationPreference() {
    }

    public NotificationPreference(UUID userId) {
        this.userId = userId;
        this.achievementNotifications = true;
        this.streakReminders = true;
        this.weeklyReports = true;
        this.challengeUpdates = true;
    }
}

