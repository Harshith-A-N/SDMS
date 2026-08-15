package com.sdms.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionRequest {

    @NotBlank(message = "rejectedBy is required")
    private String rejectedBy;

    @NotBlank(message = "rejectionReason is required")
    private String rejectionReason;
}