package com.scanshield.backend.engine;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {

    private TextNormalizer() {}

    public static String normalize(String text) {
        String n = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("\u0640", "")
                .replaceAll("[\u0623\u0625\u0622]", "\u0627")
                .replace('\u0649', '\u064A');
        return n.toLowerCase(Locale.ROOT);
    }
}