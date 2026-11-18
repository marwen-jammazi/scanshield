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
                "عاجل", "فورا", "تحذير اخير", "حسابك موقوف", "حسابك محظور", "خلال 24 ساعة"
        ), 25, "Uses urgency or threat language to pressure you");
    }
}