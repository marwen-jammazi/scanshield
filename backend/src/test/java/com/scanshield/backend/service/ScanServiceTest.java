package com.scanshield.backend.service;

import com.scanshield.backend.dto.RiskLevel;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.engine.LinkRule;
import com.scanshield.backend.engine.SensitiveInfoRule;
import com.scanshield.backend.engine.UrgencyRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScanServiceTest {

    private final ScanService service =
            new ScanService(List.of(new LinkRule(), new UrgencyRule(), new SensitiveInfoRule()));

    @Test
    void obviousScamIsHighRiskAndCappedAt100() {
        ScanResponse r = service.scan(
                "URGENT: your account suspended, confirm your identity at http://bit.ly/x1 and send your password");
        assertThat(r.score()).isEqualTo(100);
        assertThat(r.level()).isEqualTo(RiskLevel.HIGH);
        assertThat(r.reasons()).hasSize(4);
    }

    @Test
    void frenchParcelScamIsMediumRisk() {
        ScanResponse r = service.scan("Votre colis bloqué, payez sous 24h : http://poste-tn.xyz/pay");
        assertThat(r.score()).isEqualTo(65);
        assertThat(r.level()).isEqualTo(RiskLevel.MEDIUM);
    }

    @Test
    void arabicScamIsHighRisk() {
        ScanResponse r = service.scan("عاجل: حسابك موقوف ادخل رمز التحقق عبر http://192.168.4.7/login");
        assertThat(r.level()).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    void normalMessageIsLowRisk() {
        ScanResponse r = service.scan("Salut, on se voit demain au café ?");
        assertThat(r.score()).isZero();
        assertThat(r.level()).isEqualTo(RiskLevel.LOW);
        assertThat(r.reasons()).isEmpty();
    }
}