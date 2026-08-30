package com.sdms.disbursement.controller;

import com.sdms.auth.security.AuthenticatedUser;
import com.sdms.disbursement.dto.*;
import com.sdms.disbursement.service.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disbursements")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping
    public ResponseEntity<List<DisbursementResponse>> getAllDisbursements() {
        return ResponseEntity.ok(disbursementService.getAllDisbursements());
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<DisbursementResponse>> getDisbursementsByApplication(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(disbursementService.getDisbursementsByApplication(applicationId));
    }

    @PreAuthorize("hasRole('BENEFICIARY')")
    @GetMapping("/my-disbursements")
    public ResponseEntity<List<DisbursementResponse>> getMyDisbursements(Authentication authentication) {
        AuthenticatedUser authUser = (AuthenticatedUser) authentication.getDetails();
        return ResponseEntity.ok(disbursementService.getDisbursementsByBeneficiary(authUser.getBeneficiaryId()));
    }

    @PreAuthorize("hasRole('FINANCE_APPROVER')")
    @PatchMapping("/{id}/release")
    public ResponseEntity<DisbursementResponse> release(
            @PathVariable Long id,
            @RequestBody DisbursementReleaseRequest request) {
        return ResponseEntity.ok(disbursementService.release(id, request));
    }

    @PreAuthorize("hasRole('FINANCE_APPROVER')")
    @PatchMapping("/{id}/mark-failed")
    public ResponseEntity<DisbursementResponse> markAsFailed(
            @PathVariable Long id,
            @RequestBody DisbursementReleaseRequest request) {
        return ResponseEntity.ok(disbursementService.markAsFailed(id, request));
    }

    @PreAuthorize("hasRole('FINANCE_APPROVER')")
    @PatchMapping("/{id}/retry")
    public ResponseEntity<DisbursementResponse> retry(@PathVariable Long id) {
        return ResponseEntity.ok(disbursementService.retry(id));
    }
}