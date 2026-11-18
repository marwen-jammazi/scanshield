package com.scanshield.backend.engine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LinkRuleTest {

    private final LinkRule rule = new LinkRule();

    private int total(List<RuleResult> results) {
        return results.stream().mapToInt(RuleResult::points).sum();
    }

    @Test
    void noLinkMeansNoFindings() {
        assertThat(rule.evaluate("See you tomorrow at the cafe")).isEmpty();
    }

    @Test
    void plainLinkAddsFifteen() {
        assertThat(total(rule.evaluate("Check https://example.com/page"))).isEqualTo(15);
    }

    @Test
    void shortenerAddsExtraPoints() {
        assertThat(total(rule.evaluate("Click http://bit.ly/abc"))).isEqualTo(40);
    }

    @Test
    void riskyTldAddsExtraPoints() {
        assertThat(total(rule.evaluate("Pay at http://poste-tn.xyz/pay"))).isEqualTo(40);
    }

    @Test
    void rawIpAddsExtraPoints() {
        assertThat(total(rule.evaluate("Login at http://192.168.4.7/login"))).isEqualTo(45);
    }
}