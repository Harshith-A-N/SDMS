package com.sdms.application.dto;

import com.sdms.application.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private Long beneficiaryId;
    private String beneficiaryName;
    private Long schemeId;
    private String schemeName;
    private LocalDate applicationDate;
    private ApplicationStatus status;
    private Integer eligibilityScore;
    private String fieldOfficerNotes;
    private String districtOfficerNotes;
    private String financeApproverNotes;
    private String rejectedBy;
    private String rejectionReason;
    private boolean isActive;
}