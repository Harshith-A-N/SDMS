package com.sdms.beneficiary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "beneficiaries")
@Data
@NoArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable=false, unique = true)
    private String aadhaarNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    private String gender;

    private String phoneNumber;

    private String email;

    private String address;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private BigDecimal annualIncome;

    private String bankAccountNumber;

    private String ifscCode;

    @Column(nullable = false)
    private Boolean isActive = true;
}
