package com.sdms.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {

    private Long documentId;
    private Boolean nameMatch;
    private Boolean incomeMatch; // null = not applicable for this document type
    private String reason;
    private LocalDateTime verifiedAt;
}