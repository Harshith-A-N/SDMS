package com.sdms.beneficiary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponse {

    private Long id;
    private String fullName;
    private String aadhaarNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String region;
    private BigDecimal annualIncome;
    private String bankAccountNumber;
    private String ifscCode;
    private Boolean isActive;

}
