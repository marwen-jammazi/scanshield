package com.scanshield.backend.engine;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinkRule implements DetectionRule {

    private static final Pattern URL = Pattern.compile("(?i)\\b(?:https?://|www\\.)[^\\s]+");
    private static final Pattern IP_HOST = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");
    private static final Set<String> SHORTENERS =
            Set.of("bit.ly", "tinyurl.com", "t.co", "cutt.ly", "is.gd", "rb.gy", "shorturl.at");
    private static final Set<String> RISKY_TLDS =
            Set.of("xyz", "top", "click", "tk", "icu", "buzz", "cfd");

    @Override
    public List<RuleResult> evaluate(String message) {
        List<RuleResult> results = new ArrayList<>();
        Matcher m = URL.matcher(message);

        boolean hasLink = false, shortener = false, riskyTld = false, ipHost = false;

        while (m.find()) {
            hasLink = true;
            String host = extractHost(m.group());
            if (SHORTENERS.contains(host)) shortener = true;
            if (IP_HOST.matcher(host).matches()) ipHost = true;
            int dot = host.lastIndexOf('.');
            if (dot >= 0 && RISKY_TLDS.contains(host.substring(dot + 1))) riskyTld = true;
        }

        if (hasLink)   results.add(new RuleResult(15, "Message contains a link"));
        if (shortener) results.add(new RuleResult(25, "Link uses a URL shortener that hides the real destination"));
        if (riskyTld)  results.add(new RuleResult(25, "Link uses a domain extension often seen in scams"));
        if (ipHost)    results.add(new RuleResult(30, "Link points to a raw IP address instead of a domain"));
        return results;
    }

    private String extractHost(String url) {
        String s = url.replaceAll("[.,;)]+$", "")
                      .replaceFirst("(?i)^https?://", "")
                      .replaceFirst("(?i)^www\\.", "");
        return s.split("[/?#:]")[0].toLowerCase(Locale.ROOT);
    }
}