package com.scanshield.backend.engine;

import com.scanshield.backend.ml.MlClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adds points when the ML model finds the text similar to known scams.
 * It can only add points, never remove them, and it is skipped when the service is unavailable.
 */
@Component
public class MlRule implements DetectionRule {

    private final MlClient client;
    private final double threshold;
    private final int maxPoints;

    public MlRule(
            MlClient client,
            @Value("${scamshield.ml.threshold:0.6}") double threshold,
            @Value("${scamshield.ml.max-points:30}") int maxPoints) {
        this.client = client;
        this.threshold = threshold;
        this.maxPoints = maxPoints;
    }

    @Override
    public List<RuleResult> evaluate(String message) {
        Optional<Double> probability = client.scamProbability(message);
        if (probability.isEmpty() || probability.get() < threshold) {
            return List.of();
        }
        double p = probability.get();
        int points = (int) Math.round(p * maxPoints);
        String reason = "Text closely resembles known scam messages (model confidence "
                + Math.round(p * 100) + "%)";
        return List.of(new RuleResult(points, reason));
    }
}