package com.sdms.disbursement.dto;

import com.sdms.disbursement.entity.DisbursementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementResponse {

    private Long id;
    private Long applicationId;
    private String beneficiaryName;
    private String schemeName;
    private Integer milestoneNumber;
    private BigDecimal amount;
    private DisbursementStatus status;
    private LocalDate disbursementDate;
    private String remarks;
}