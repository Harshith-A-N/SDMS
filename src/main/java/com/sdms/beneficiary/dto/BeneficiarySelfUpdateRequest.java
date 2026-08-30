package com.sdms.beneficiary.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class BeneficiarySelfUpdateRequest {
    private String phoneNumber;

    @Email(message = "Email must be valid")
    private String email;

    private String address;
    private String bankAccountNumber;
    private String ifscCode;
}