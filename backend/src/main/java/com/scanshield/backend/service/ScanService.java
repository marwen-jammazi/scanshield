package com.scanshield.backend.service;

import com.scanshield.backend.dto.RiskLevel;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.engine.DetectionRule;
import com.scanshield.backend.engine.MlRule;
import com.scanshield.backend.engine.RuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScanService {

    private final List<DetectionRule> rules;
    private final MlRule ml;

    @Autowired
    public ScanService(List<DetectionRule> rules, MlRule ml) {
        this.rules = rules;
        this.ml = ml;
    }

    // Rules only, without the ML model
    public ScanService(List<DetectionRule> rules) {
        this(rules, null);
    }

    public ScanResponse scan(String message) {
        List<RuleResult> results = new ArrayList<>(
                rules.stream().flatMap(rule -> rule.evaluate(message).stream()).toList());

        Double mlProbability = null;
        if (ml != null) {
            Optional<Double> probability = ml.probability(message);
            if (probability.isPresent()) {
                mlProbability = probability.get();
                ml.finding(mlProbability).ifPresent(results::add);
            }
        }

        int score = Math.min(100, results.stream().mapToInt(RuleResult::points).sum());
        List<String> reasons = results.stream().map(RuleResult::reason).toList();

        return new ScanResponse(score, toLevel(score), reasons, mlProbability);
    }

    private RiskLevel toLevel(int score) {
        if (score >= 70) return RiskLevel.HIGH;
        if (score >= 40) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}