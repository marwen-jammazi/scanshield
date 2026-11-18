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
                "كلمة السر", "كلمة المرور", "رمز التحقق", "رقم البطاقة", "بطاقتك البنكية"
        ), 35, "Asks for sensitive information such as passwords, codes or card details");
    }
}