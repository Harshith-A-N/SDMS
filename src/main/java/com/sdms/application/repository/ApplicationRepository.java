package com.sdms.application.repository;

import com.sdms.application.entity.Application;
import com.sdms.application.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByBeneficiaryId(Long beneficiaryId);

    List<Application> findBySchemeId(Long schemeId);

    // for the reapply check
    Optional<Application> findByBeneficiaryIdAndSchemeIdAndIsActiveTrueAndStatusNot( // StatusNot => status is not equal to whatever is passed
            Long beneficiaryId, Long schemeId, ApplicationStatus status);
}