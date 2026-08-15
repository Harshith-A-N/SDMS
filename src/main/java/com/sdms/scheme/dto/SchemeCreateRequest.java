package com.sdms.scheme.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SchemeCreateRequest {

    private String name;
    private String description;
    private BigDecimal minIncome;
    private BigDecimal maxIncome;
    private BigDecimal grantAmount;
    private String region;
    private BigDecimal totalBudget;
    // id and isActive is deliberately excluded
}
