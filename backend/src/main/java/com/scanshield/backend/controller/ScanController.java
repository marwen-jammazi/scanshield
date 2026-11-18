package com.scanshield.backend.controller;

import com.scanshield.backend.dto.ScanHistoryItem;
import com.scanshield.backend.dto.ScanRequest;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.service.ScanHistoryService;
import jakarta.validation.Valid;
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

    @PostMapping("/scan")
    public ScanResponse scan(@Valid @RequestBody ScanRequest request) {
        return historyService.scanAndSave(request.message());
    }

    @GetMapping("/scans")
    public List<ScanHistoryItem> history() {
        return historyService.recent();
    }
}