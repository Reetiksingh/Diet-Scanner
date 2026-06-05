package com.nutrilens.nutrition.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "scan_jobs", schema = "nutrition")
public class ScanJob {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "scan_type")
    private String scanType;
    @Column(name = "raw_text", columnDefinition = "text")
    private String rawText;
    @Column(nullable = false)
    private String status;
    @Column(name = "result_payload", columnDefinition = "text")
    private String resultPayload;
    @Column(name = "created_at")
    private Instant createdAt;

    protected ScanJob() {
    }

    public ScanJob(UUID userId, String scanType, String rawText, String resultPayload) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.scanType = scanType;
        this.rawText = rawText;
        this.status = "COMPLETED";
        this.resultPayload = resultPayload;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getStatus() { return status; }
    public String getResultPayload() { return resultPayload; }
}

