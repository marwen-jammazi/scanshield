package com.scanshield.backend.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizerTest {

    @Test
    void stripsFrenchAccents() {
        assertThat(TextNormalizer.normalize("Bloqué")).isEqualTo("bloque");
    }

    @Test
    void lowercasesText() {
        assertThat(TextNormalizer.normalize("URGENT")).isEqualTo("urgent");
    }

    @Test
    void unifiesArabicAlefVariants() {
        assertThat(TextNormalizer.normalize("\u0623\u062D\u0645\u062F"))
                .isEqualTo(TextNormalizer.normalize("\u0627\u062D\u0645\u062F"));
    }
}