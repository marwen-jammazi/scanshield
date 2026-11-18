package com.scanshield.backend.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordRulesTest {

    private final UrgencyRule urgency = new UrgencyRule();
    private final SensitiveInfoRule sensitive = new SensitiveInfoRule();

    @Test
    void detectsEnglishUrgency() {
        assertThat(urgency.evaluate("URGENT: act now")).hasSize(1);
    }

    @Test
    void detectsFrenchUrgencyWithAccents() {
        assertThat(urgency.evaluate("Votre colis est bloqué, payez sous 24h")).hasSize(1);
    }

    @Test
    void detectsArabicUrgency() {
        assertThat(urgency.evaluate("عاجل: حسابك موقوف")).hasSize(1);
    }

    @Test
    void detectsSensitiveInfoRequest() {
        assertThat(sensitive.evaluate("Send your password and OTP")).hasSize(1);
    }

    @Test
    void ignoresNormalMessages() {
        assertThat(urgency.evaluate("Salut, on se voit demain au café ?")).isEmpty();
        assertThat(sensitive.evaluate("Salut, on se voit demain au café ?")).isEmpty();
    }
}