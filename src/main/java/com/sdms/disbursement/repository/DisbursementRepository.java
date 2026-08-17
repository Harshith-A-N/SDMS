package com.sdms.disbursement.repository;

import com.sdms.disbursement.entity.Disbursement;
import com.sdms.disbursement.entity.DisbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {

    List<Disbursement> findByApplicationId(Long applicationId);

    List<Disbursement> findByApplicationIdOrderByMilestoneNumberAsc(Long applicationId);

    Optional<Disbursement> findByApplicationIdAndMilestoneNumber(Long applicationId, Integer milestoneNumber);

    List<Disbursement> findByStatus(DisbursementStatus status);
}
