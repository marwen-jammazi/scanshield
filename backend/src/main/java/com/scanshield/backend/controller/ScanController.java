package com.scanshield.backend.controller;

import com.scanshield.backend.dto.ScanHistoryItem;
import com.scanshield.backend.dto.ScanRequest;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.service.ScanHistoryService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScanController {

    private final ScanHistoryService historyService;

    public ScanController(ScanHistoryService historyService) {
        this.historyService = historyService;
    }

    // Public endpoint: jwt is null for anonymous callers, so nothing is saved for them
    @PostMapping("/scan")
    public ScanResponse scan(@Valid @RequestBody ScanRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt == null ? null : Long.valueOf(jwt.getSubject());
        return historyService.scanAndSave(request.message(), userId);
    }

    // Protected endpoint: only ever returns the caller's own scans
    @GetMapping("/scans")
    public List<ScanHistoryItem> history(@AuthenticationPrincipal Jwt jwt) {
        return historyService.recent(Long.valueOf(jwt.getSubject()));
    }
}