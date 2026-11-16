package com.scanshield.backend.dto;

import java.util.List;

public record ScanResponse(
        int score,
        RiskLevel level,
        List<String> reasons
) {}