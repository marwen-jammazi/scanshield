package com.scanshield.backend.service;

import com.scanshield.backend.dto.ScanHistoryItem;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.model.ScanRecord;
import com.scanshield.backend.repository.ScanRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScanHistoryService {

    private final ScanService scanService;
    private final ScanRecordRepository repository;

    public ScanHistoryService(ScanService scanService, ScanRecordRepository repository) {
        this.scanService = scanService;
        this.repository = repository;
    }

    public ScanResponse scanAndSave(String message) {
        ScanResponse response = scanService.scan(message);
        repository.save(new ScanRecord(message, response.score(), response.level(), response.reasons()));
        return response;
    }

    public List<ScanHistoryItem> recent() {
        return repository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(r -> new ScanHistoryItem(
                        r.getId(), r.getMessage(), r.getScore(),
                        r.getLevel(), r.getReasons(), r.getCreatedAt()))
                .toList();
    }
}