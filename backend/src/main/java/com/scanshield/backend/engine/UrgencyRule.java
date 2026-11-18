package com.scanshield.backend.engine;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UrgencyRule extends KeywordRule {
    public UrgencyRule() {
        super(List.of(
                // English
                "urgent", "immediately", "within 24 hours", "last warning",
                "account suspended", "account blocked", "act now",
                // French
                "immediatement", "dernier avertissement", "compte suspendu",
                "compte bloque", "colis bloque", "sous 24h",
                // Arabic
                "عاجل", "فورا", "تحذير اخير", "حسابك موقوف", "حسابك محظور", "خلال 24 ساعة",
                // Arabizi (Arabic in Latin letters) and Tunisian mixed with French
                "3ajel", "fawran", "ta7dhir a5ir", "7sabek mawkouf", "7sabek ma7dhour",
                "7sabek bloque", "7sabek suspendu", "compte mte3ek bloque",
                "colis mte3ek bloque", "khilel 24 sa3a"
        ), 25, "Uses urgency or threat language to pressure you");
    }
}