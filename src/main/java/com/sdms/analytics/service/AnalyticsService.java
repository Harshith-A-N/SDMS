package com.sdms.analytics.service;

import com.sdms.analytics.dto.*;
import com.sdms.application.entity.ApplicationStatus;
import com.sdms.application.repository.ApplicationRepository;
import com.sdms.disbursement.entity.DisbursementStatus;
import com.sdms.disbursement.repository.DisbursementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ApplicationRepository applicationRepository;
    private final DisbursementRepository disbursementRepository;

    public List<SchemeFundUtilizationResponse> getSchemeFundUtilization() {

        List<SchemeSanctionedProjection> sanctionedRows = applicationRepository.countSanctionedPerScheme();
        List<SchemeDisbursedProjection> disbursedRows = disbursementRepository.sumDisbursedPerScheme();

        // build a lookup: schemeId -> totalDisbursed, so we can merge by scheme
        Map<Long, BigDecimal> disbursedByScheme = new HashMap<>();
        for (SchemeDisbursedProjection row : disbursedRows) {
            disbursedByScheme.put(row.getSchemeId(), row.getTotalDisbursed());
        }

        return sanctionedRows.stream()
                .map(row -> {
                    BigDecimal totalSanctioned = row.getGrantAmount()
                            .multiply(BigDecimal.valueOf(row.getSanctionedCount()));

                    // scheme might have sanctioned applications but zero released disbursements yet
                    BigDecimal totalDisbursed = disbursedByScheme.getOrDefault(row.getSchemeId(), BigDecimal.ZERO);

                    return new SchemeFundUtilizationResponse(
                            row.getSchemeId(),
                            row.getSchemeName(),
                            totalSanctioned,
                            totalDisbursed
                    );
                })
                .toList();
    }

    public List<StatusCountResponse> getApplicationCountByStatus() {

        List<StatusCountProjection> rows = applicationRepository.countApplicationsByStatus();

        // build lookup: status -> count, from whatever the query actually returned
        Map<ApplicationStatus, Long> countsFound = new HashMap<>();
        for (StatusCountProjection row : rows) {
            countsFound.put(ApplicationStatus.valueOf(row.getStatus()), row.getCount());
        }

        // fill in ALL statuses, defaulting missing ones to 0
        List<StatusCountResponse> result = new ArrayList<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            Long count = countsFound.getOrDefault(status, 0L);
            result.add(new StatusCountResponse(status, count));
        }

        return result;
    }

    public PendingVsReleasedResponse getPendingVsReleased() {
        BigDecimal totalPending = disbursementRepository.sumAmountByStatus(DisbursementStatus.PENDING);
        BigDecimal totalReleased = disbursementRepository.sumAmountByStatus(DisbursementStatus.RELEASED);

        return new PendingVsReleasedResponse(
                totalPending != null ? totalPending : BigDecimal.ZERO,
                totalReleased != null ? totalReleased : BigDecimal.ZERO
        );
    }
}