package com.scanshield.backend.engine;

import com.scanshield.backend.ml.MlClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Wraps the ML model. The probability is always reported to the user when available;
 * points are only added when it passes the threshold, and the model can never remove points.
 */
@Component
public class MlRule {

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

    public Optional<Double> probability(String message) {
        return client.scamProbability(message);
    }

    public Optional<RuleResult> finding(double probability) {
        if (probability < threshold) {
            return Optional.empty();
        }
        int points = (int) Math.round(probability * maxPoints);
        return Optional.of(new RuleResult(points, "The AI model finds this text similar to known scam messages"));
    }
}