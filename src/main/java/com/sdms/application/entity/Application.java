package com.sdms.application.entity;

import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.scheme.entity.Scheme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private Beneficiary beneficiary;

    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    @Column(nullable = false)
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private Integer eligibilityScore;

    @Column(length = 1000)
    private String fieldOfficerNotes;

    @Column(length = 1000)
    private String districtOfficerNotes;

    @Column(length = 1000)
    private String financeApproverNotes;

    private String rejectedBy;

    @Column(length = 1000)
    private String rejectionReason;

    private boolean isActive = true;
}