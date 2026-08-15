package com.sdms.scheme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemeResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal minIncome;
    private BigDecimal maxIncome;
    private BigDecimal grantAmount;
    private String region;
    private BigDecimal totalBudget;
    private Boolean isActive;

    // This one does include id and isActive — because the client needs to see them in the response (e.g., to reference the scheme later, or check if it's active).
}
