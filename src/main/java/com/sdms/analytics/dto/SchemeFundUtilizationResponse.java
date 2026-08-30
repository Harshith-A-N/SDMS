package com.sdms.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemeFundUtilizationResponse {

    private Long schemeId;
    private String schemeName;
    private BigDecimal totalSanctioned;
    private BigDecimal totalDisbursed;
}