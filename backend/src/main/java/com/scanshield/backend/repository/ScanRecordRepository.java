package com.scanshield.backend.repository;

import com.scanshield.backend.model.ScanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScanRecordRepository extends JpaRepository<ScanRecord, Long> {

    List<ScanRecord> findTop20ByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}