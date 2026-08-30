package com.sdms.analytics.controller;

import com.sdms.analytics.dto.PendingVsReleasedResponse;
import com.sdms.analytics.dto.SchemeFundUtilizationResponse;
import com.sdms.analytics.dto.StatusCountResponse;
import com.sdms.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/scheme-fund-utilization")
    public ResponseEntity<List<SchemeFundUtilizationResponse>> getSchemeFundUtilization() {
        return ResponseEntity.ok(analyticsService.getSchemeFundUtilization());
    }

    @GetMapping("/applications-by-status")
    public ResponseEntity<List<StatusCountResponse>> getApplicationCountByStatus() {
        return ResponseEntity.ok(analyticsService.getApplicationCountByStatus());
    }

    @GetMapping("/pending-vs-released")
    public ResponseEntity<PendingVsReleasedResponse> getPendingVsReleased() {
        return ResponseEntity.ok(analyticsService.getPendingVsReleased());
    }
}