package com.sdms.disbursement.repository;

import com.sdms.analytics.dto.SchemeDisbursedProjection;
import com.sdms.disbursement.entity.Disbursement;
import com.sdms.disbursement.entity.DisbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {

    List<Disbursement> findByApplicationId(Long applicationId);

    List<Disbursement> findByApplicationIdOrderByMilestoneNumberAsc(Long applicationId);

    Optional<Disbursement> findByApplicationIdAndMilestoneNumber(Long applicationId, Integer milestoneNumber);

    List<Disbursement> findByStatus(DisbursementStatus status);

    @Query("SELECT d.application.scheme.id AS schemeId, d.application.scheme.name AS schemeName, " +
            "SUM(d.amount) AS totalDisbursed FROM Disbursement d WHERE d.status = 'RELEASED' " +
            "GROUP BY d.application.scheme.id, d.application.scheme.name")
    List<SchemeDisbursedProjection> sumDisbursedPerScheme();

    @Query("SELECT SUM(d.amount) FROM Disbursement d WHERE d.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") DisbursementStatus status);

    List<Disbursement> findByApplication_Beneficiary_Id(Long beneficiaryId);
}
