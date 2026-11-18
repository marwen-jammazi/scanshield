package com.scanshield.backend.engine;

import java.util.List;

public interface DetectionRule {
    List<RuleResult> evaluate(String message);
}