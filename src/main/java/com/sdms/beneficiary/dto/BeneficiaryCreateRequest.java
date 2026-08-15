package com.sdms.beneficiary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BeneficiaryCreateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(regexp = "\\d{12}", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String gender;

    @Pattern(regexp = "[6-9]\\d{9}", message = "Phone number must be a valid 10-digit number")
    @NotBlank
    private String phoneNumber;

    @Email(message = "Email must be a valid email address")
    private String email;

    private String address;
    private String region;
    private BigDecimal annualIncome;
    private String bankAccountNumber;
    private String ifscCode;
}