package com.scanshield.backend.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordBoundaryTest {

    private final SensitiveInfoRule sensitive = new SensitiveInfoRule();

    @Test
    void doesNotMatchKeywordInsideLongerWord() {
        assertThat(sensitive.evaluate("We ordered hotpot for dinner")).isEmpty();
    }

    @Test
    void stillMatchesKeywordAsWholeWord() {
        assertThat(sensitive.evaluate("Send me the OTP now")).hasSize(1);
    }

    @Test
    void arabicKeywordStillMatchesWithAttachedPrefix() {
        // "\u0648" (and) is glued to the next word in Arabic: \u0648\u0639\u0627\u062C\u0644
        UrgencyRule urgency = new UrgencyRule();
        assertThat(urgency.evaluate("\u0648\u0639\u0627\u062C\u0644")).hasSize(1);
    }
}