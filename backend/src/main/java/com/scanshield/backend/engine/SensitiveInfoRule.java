package com.scanshield.backend.engine;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SensitiveInfoRule extends KeywordRule {
    public SensitiveInfoRule() {
        super(List.of(
                // English
                "password", "verification code", "otp", "cvv", "card number", "confirm your identity",
                // French
                "mot de passe", "code de verification", "numero de carte", "carte bancaire", "confirmez votre identite",
                // Arabic
                "كلمة السر", "كلمة المرور", "رمز التحقق", "رقم البطاقة", "بطاقتك البنكية",
                // Arabizi and Tunisian mixed with French
                "ab3eth el code", "ab3ath el code", "a3tina el code", "kelmet el sir",
                "kalimat el sir", "ra9m el carte", "ra9m bita9a", "ra9m el bita9a"
        ), 35, "Asks for sensitive information such as passwords, codes or card details");
    }
}