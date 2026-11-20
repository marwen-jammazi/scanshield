package com.scanshield.backend.dto;

import java.util.List;

/**
 * mlProbability is the ML model's scam probability (0 to 1), or null when the model was unavailable.
 */
public record ScanResponse(int score, RiskLevel level, List<String> reasons, Double mlProbability) {

    public ScanResponse(int score, RiskLevel level, List<String> reasons) {
        this(score, level, reasons, null);
    }
}