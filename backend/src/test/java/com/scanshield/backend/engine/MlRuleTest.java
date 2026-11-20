package com.scanshield.backend.engine;

import com.scanshield.backend.ml.MlClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MlRuleTest {

    private final MlClient client = mock(MlClient.class);
    private final MlRule rule = new MlRule(client, 0.6, 30);

    @Test
    void noFindingWhenTheServiceIsUnavailable() {
        when(client.scamProbability("hi")).thenReturn(Optional.empty());

        assertThat(rule.evaluate("hi")).isEmpty();
    }

    @Test
    void noFindingBelowTheThreshold() {
        when(client.scamProbability("hi")).thenReturn(Optional.of(0.59));

        assertThat(rule.evaluate("hi")).isEmpty();
    }

    @Test
    void addsScaledPointsAndAnExplanationAboveTheThreshold() {
        when(client.scamProbability("hi")).thenReturn(Optional.of(0.87));

        List<RuleResult> results = rule.evaluate("hi");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).points()).isEqualTo(26);
        assertThat(results.get(0).reason()).contains("87%");
    }
}