package com.nutrilens.achievement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "achievements", schema = "gamification")
public class Achievement {
    @Id
    private UUID id;
    private String code;
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(name = "rule_type")
    private String ruleType;
    @Column(name = "rule_config", columnDefinition = "text")
    private String ruleConfig;
    private boolean active;

    protected Achievement() {
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRuleType() { return ruleType; }
    public String getRuleConfig() { return ruleConfig; }
}
