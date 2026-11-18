package com.scanshield.backend.service;

import com.scanshield.backend.dto.RiskLevel;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.engine.DetectionRule;
import com.scanshield.backend.engine.RuleResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScanService {

    private final List<DetectionRule> rules;

    public ScanService(List<DetectionRule> rules) {
        this.rules = rules;
    }

    public ScanResponse scan(String message) {
        List<RuleResult> findings = rules.stream()
                .flatMap(rule -> rule.evaluate(message).stream())
                .toList();

        int score = Math.min(100, findings.stream().mapToInt(RuleResult::points).sum());
        List<String> reasons = findings.stream().map(RuleResult::reason).toList();

        return new ScanResponse(score, toLevel(score), reasons);
    }

    private RiskLevel toLevel(int score) {
        if (score >= 70) return RiskLevel.HIGH;
        if (score >= 40) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}