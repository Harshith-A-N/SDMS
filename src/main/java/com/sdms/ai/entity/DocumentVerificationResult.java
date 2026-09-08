package com.sdms.ai.entity;

import com.sdms.document.entity.ApplicationDocument;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_verification_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerificationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private ApplicationDocument document;

    @Column(nullable = false)
    private Boolean nameMatch;

    // Nullable on purpose: only INCOME_CERTIFICATE documents have income info to check.
    // null = not applicable for this document type, not "unknown"/"failed".
    @Column
    private Boolean incomeMatch;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;
}