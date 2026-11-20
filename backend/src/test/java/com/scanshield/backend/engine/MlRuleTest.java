package com.scanshield.backend.engine;

import com.scanshield.backend.ml.MlClient;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MlRuleTest {

    private final MlClient client = mock(MlClient.class);
    private final MlRule rule = new MlRule(client, 0.6, 30);

    @Test
    void probabilityComesFromTheClient() {
        when(client.scamProbability("hi")).thenReturn(Optional.of(0.42));

        assertThat(rule.probability("hi")).contains(0.42);
    }

    @Test
    void noFindingBelowTheThreshold() {
        assertThat(rule.finding(0.59)).isEmpty();
    }

    @Test
    void addsScaledPointsAboveTheThreshold() {
        assertThat(rule.finding(0.87)).hasValueSatisfying(r -> assertThat(r.points()).isEqualTo(26));
    }
}