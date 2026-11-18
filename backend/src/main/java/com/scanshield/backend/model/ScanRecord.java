package com.scanshield.backend.model;

import com.scanshield.backend.dto.RiskLevel;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scan_records")
public class ScanRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String message;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RiskLevel level;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scan_record_reasons", joinColumns = @JoinColumn(name = "scan_record_id"))
    @Column(name = "reason", length = 300)
    private List<String> reasons = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected ScanRecord() {}

    public ScanRecord(String message, int score, RiskLevel level, List<String> reasons) {
        this.message = message;
        this.score = score;
        this.level = level;
        this.reasons = new ArrayList<>(reasons);
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public int getScore() { return score; }
    public RiskLevel getLevel() { return level; }
    public List<String> getReasons() { return reasons; }
    public Instant getCreatedAt() { return createdAt; }
}