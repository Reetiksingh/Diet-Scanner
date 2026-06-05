package com.nutrilens.common.domain;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        String path,
        int status,
        String code,
        String message,
        List<FieldViolation> details,
        String requestId
) {
    public record FieldViolation(String field, String message) {
    }
}

