package com.scanshield.backend.dto;

import java.time.Instant;
import java.util.List;

public record ScanHistoryItem(
        Long id,
        String message,
        int score,
        RiskLevel level,
        List<String> reasons,
        Instant createdAt
) {}