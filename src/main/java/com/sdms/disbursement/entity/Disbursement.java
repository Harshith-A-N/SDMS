package com.sdms.disbursement.entity;

import com.sdms.application.entity.Application;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "disbursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private Integer milestoneNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisbursementStatus status;

    private LocalDate disbursementDate;

    @Column(length = 500)
    private String remarks;
}