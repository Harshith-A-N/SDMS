package com.sdms.scheme.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "schemes")
@Data
@NoArgsConstructor
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "min_income")
    private BigDecimal minIncome;

    @Column(name = "max_income")
    private BigDecimal maxIncome;

    @Column(name = "grant_amount", nullable = false)
    private BigDecimal grantAmount;

    @Column(name = "region")
    private String region;

    @Column(name = "total_budget")
    private BigDecimal totalBudget;

    @Column(name = "is_active")
    private Boolean isActive = true;
}