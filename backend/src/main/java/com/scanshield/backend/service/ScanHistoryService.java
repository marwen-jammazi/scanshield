package com.scanshield.backend.service;

import com.scanshield.backend.dto.ScanHistoryItem;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.model.ScanRecord;
import com.scanshield.backend.repository.AppUserRepository;
import com.scanshield.backend.repository.ScanRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScanHistoryService {

    private final ScanService scanService;
    private final ScanRecordRepository scans;
    private final AppUserRepository users;

    public ScanHistoryService(ScanService scanService, ScanRecordRepository scans, AppUserRepository users) {
        this.scanService = scanService;
        this.scans = scans;
        this.users = users;
    }

    /** Scans the message; saves it only when it belongs to a known logged-in user. */
    public ScanResponse scanAndSave(String message, Long userId) {
        ScanResponse response = scanService.scan(message);
        if (userId != null && users.existsById(userId)) {
            scans.save(new ScanRecord(users.getReferenceById(userId), message,
                    response.score(), response.level(), response.reasons()));
        }
        return response;
    }

    public List<ScanHistoryItem> recent(Long userId) {
        return scans.findTop20ByOwnerIdOrderByCreatedAtDesc(userId).stream()
                .map(r -> new ScanHistoryItem(
                        r.getId(), r.getMessage(), r.getScore(),
                        r.getLevel(), r.getReasons(), r.getCreatedAt()))
                .toList();
    }
}