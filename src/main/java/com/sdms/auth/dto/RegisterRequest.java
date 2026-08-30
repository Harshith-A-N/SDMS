package com.sdms.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegisterRequest {

    // Login fields
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Shared + Beneficiary fields
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Aadhaar number is required")
    private String aadhaarNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private String gender;

    private String phoneNumber;

    @Email(message = "Email must be valid")
    private String email;

    private String address;

    @NotBlank(message = "Region is required")
    private String region;

    @NotNull(message = "Annual income is required")
    @PositiveOrZero(message = "Annual income cannot be negative")
    private BigDecimal annualIncome;

    private String bankAccountNumber;

    private String ifscCode;
}