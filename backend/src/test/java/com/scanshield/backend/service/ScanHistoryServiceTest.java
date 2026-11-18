package com.scanshield.backend.service;

import com.scanshield.backend.dto.RiskLevel;
import com.scanshield.backend.dto.ScanResponse;
import com.scanshield.backend.model.AppUser;
import com.scanshield.backend.model.ScanRecord;
import com.scanshield.backend.repository.AppUserRepository;
import com.scanshield.backend.repository.ScanRecordRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ScanHistoryServiceTest {

    private final ScanService scanService = mock(ScanService.class);
    private final ScanRecordRepository scans = mock(ScanRecordRepository.class);
    private final AppUserRepository users = mock(AppUserRepository.class);
    private final ScanHistoryService service = new ScanHistoryService(scanService, scans, users);

    private final ScanResponse response = new ScanResponse(65, RiskLevel.MEDIUM, List.of("reason"));

    @Test
    void anonymousScanIsReturnedButNotSaved() {
        when(scanService.scan("hi")).thenReturn(response);

        assertThat(service.scanAndSave("hi", null)).isEqualTo(response);

        verifyNoInteractions(scans);
    }

    @Test
    void loggedInScanIsSaved() {
        when(scanService.scan("hi")).thenReturn(response);
        when(users.existsById(7L)).thenReturn(true);
        when(users.getReferenceById(7L)).thenReturn(new AppUser("a@example.com", "hash"));

        service.scanAndSave("hi", 7L);

        verify(scans).save(any(ScanRecord.class));
    }

    @Test
    void scanForUnknownUserIsNotSaved() {
        when(scanService.scan("hi")).thenReturn(response);
        when(users.existsById(99L)).thenReturn(false);

        assertThat(service.scanAndSave("hi", 99L)).isEqualTo(response);

        verify(scans, never()).save(any());
    }

    @Test
    void historyIsQueriedForTheOwnerOnly() {
        when(scans.findTop20ByOwnerIdOrderByCreatedAtDesc(7L)).thenReturn(List.of());

        assertThat(service.recent(7L)).isEmpty();

        verify(scans).findTop20ByOwnerIdOrderByCreatedAtDesc(7L);
    }
}