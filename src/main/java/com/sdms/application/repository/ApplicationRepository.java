package com.sdms.application.repository;

import com.sdms.analytics.dto.SchemeSanctionedProjection;
import com.sdms.analytics.dto.StatusCountProjection;
import com.sdms.application.entity.Application;
import com.sdms.application.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByBeneficiaryId(Long beneficiaryId);

    List<Application> findBySchemeId(Long schemeId);

    // for the reapply check
    Optional<Application> findByBeneficiaryIdAndSchemeIdAndIsActiveTrueAndStatusNot( // StatusNot => status is not equal to whatever is passed
            Long beneficiaryId, Long schemeId, ApplicationStatus status);

    @Query("SELECT a.status AS status, COUNT(a) AS count FROM Application a GROUP BY a.status")
    List<StatusCountProjection> countApplicationsByStatus();

    @Query("SELECT a.scheme.id AS schemeId, a.scheme.name AS schemeName, " +
            "COUNT(a) AS sanctionedCount, a.scheme.grantAmount AS grantAmount " +
            "FROM Application a WHERE a.status IN ('APPROVED', 'DISBURSED') " +
            "GROUP BY a.scheme.id, a.scheme.name, a.scheme.grantAmount")
    List<SchemeSanctionedProjection> countSanctionedPerScheme();
}