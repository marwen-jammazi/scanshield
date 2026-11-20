package com.scanshield.backend.service;

import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.engine.LinkRule;
import com.scanshield.backend.engine.MlRule;
import com.scanshield.backend.ml.MlClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScanServiceMlTest {

    private final MlClient client = mock(MlClient.class);
    private final ScanService service =
            new ScanService(List.of(new LinkRule()), new MlRule(client, 0.6, 30));

    @Test
    void lowProbabilityIsStillShownButAddsNoPoints() {
        when(client.scamProbability("Salut")).thenReturn(Optional.of(0.2));

        ScanResponse r = service.scan("Salut");

        assertThat(r.mlProbability()).isEqualTo(0.2);
        assertThat(r.score()).isZero();
    }

    @Test
    void highProbabilityIsShownAndAddsPoints() {
        when(client.scamProbability("Salut")).thenReturn(Optional.of(0.9));

        ScanResponse r = service.scan("Salut");

        assertThat(r.mlProbability()).isEqualTo(0.9);
        assertThat(r.score()).isEqualTo(27);
        assertThat(r.reasons()).hasSize(1);
    }

    @Test
    void unavailableModelLeavesTheProbabilityEmpty() {
        when(client.scamProbability("Salut")).thenReturn(Optional.empty());

        ScanResponse r = service.scan("Salut");

        assertThat(r.mlProbability()).isNull();
        assertThat(r.score()).isZero();
    }
}