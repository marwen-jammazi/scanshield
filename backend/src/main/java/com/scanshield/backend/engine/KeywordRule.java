package com.scanshield.backend.engine;

import java.util.List;
import java.util.regex.Pattern;

public abstract class KeywordRule implements DetectionRule {

    private final List<Pattern> patterns;
    private final int points;
    private final String reason;

    protected KeywordRule(List<String> keywords, int points, String reason) {
        this.patterns = keywords.stream()
                .map(TextNormalizer::normalize)
                .map(KeywordRule::toPattern)
                .toList();
        this.points = points;
        this.reason = reason;
    }

    // Latin keywords must match as whole words ("otp" must not match "hotpot").
    // Non-Latin (Arabic) keywords match as substrings, since prefixes attach to words.
    private static Pattern toPattern(String keyword) {
        String quoted = Pattern.quote(keyword);
        boolean latin = keyword.chars().allMatch(c -> c < 128);
        return latin
                ? Pattern.compile("(?<![\\p{L}\\p{N}])" + quoted + "(?![\\p{L}\\p{N}])")
                : Pattern.compile(quoted);
    }

    @Override
    public List<RuleResult> evaluate(String message) {
        String text = TextNormalizer.normalize(message);
        boolean hit = patterns.stream().anyMatch(p -> p.matcher(text).find());
        return hit ? List.of(new RuleResult(points, reason)) : List.of();
    }
}