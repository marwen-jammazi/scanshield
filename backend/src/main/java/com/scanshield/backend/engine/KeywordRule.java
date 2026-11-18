package com.scanshield.backend.engine;

import java.util.List;

public abstract class KeywordRule implements DetectionRule {

    private final List<String> keywords;
    private final int points;
    private final String reason;

    protected KeywordRule(List<String> keywords, int points, String reason) {
        this.keywords = keywords.stream().map(TextNormalizer::normalize).toList();
        this.points = points;
        this.reason = reason;
    }

    @Override
    public List<RuleResult> evaluate(String message) {
        String text = TextNormalizer.normalize(message);
        boolean hit = keywords.stream().anyMatch(text::contains);
        return hit ? List.of(new RuleResult(points, reason)) : List.of();
    }
}