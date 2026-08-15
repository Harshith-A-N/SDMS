package com.sdms.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationCreateRequest {

    @NotNull(message = "beneficiaryId is required")
    private Long beneficiaryId;

    @NotNull(message = "schemeId is required")
    private Long schemeId;
}