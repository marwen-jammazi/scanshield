package com.scanshield.backend.engine;

import com.scanshield.backend.dto.RiskLevel;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.service.ScanService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArabiziTest {

    private final UrgencyRule urgency = new UrgencyRule();
    private final SensitiveInfoRule sensitive = new SensitiveInfoRule();
    private final ScanService service =
            new ScanService(List.of(new LinkRule(), urgency, sensitive));

    @Test
    void detectsArabiziUrgency() {
        assertThat(urgency.evaluate("Salam, 7sabek mawkouf")).hasSize(1);
    }

    @Test
    void detectsMixedFrenchAndTunisianUrgency() {
        assertThat(urgency.evaluate("Compte mte3ek bloqué")).hasSize(1);
    }

    @Test
    void detectsArabiziCodeRequest() {
        assertThat(sensitive.evaluate("Ab3eth el code tawa")).hasSize(1);
    }

    @Test
    void ordinaryArabiziChatIsNotFlagged() {
        String chat = "Salam, chnowa a7wellek? nchoufek ghodwa";
        assertThat(urgency.evaluate(chat)).isEmpty();
        assertThat(sensitive.evaluate(chat)).isEmpty();
    }

    @Test
    void arabiziScamIsHighRisk() {
        ScanResponse r = service.scan("Salam, 7sabek mawkouf, ab3eth el code fi http://bit.ly/x1");
        assertThat(r.level()).isEqualTo(RiskLevel.HIGH);
        assertThat(r.score()).isEqualTo(100);
    }
}