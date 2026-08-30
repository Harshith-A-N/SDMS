package com.sdms.application.controller;

import com.sdms.application.dto.*;
import com.sdms.application.service.ApplicationService;
import com.sdms.auth.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PreAuthorize("hasRole('FIELD_OFFICER')")
    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @Valid @RequestBody ApplicationCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/beneficiary/{beneficiaryId}")
    public ResponseEntity<List<ApplicationResponse>> getByBeneficiary(@PathVariable Long beneficiaryId) {
        return ResponseEntity.ok(applicationService.getApplicationsByBeneficiary(beneficiaryId));
    }

    @PreAuthorize("hasRole('BENEFICIARY')")
    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(Authentication authentication) {
        AuthenticatedUser authUser = (AuthenticatedUser) authentication.getDetails();
        return ResponseEntity.ok(applicationService.getApplicationsByBeneficiary(authUser.getBeneficiaryId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/scheme/{schemeId}")
    public ResponseEntity<List<ApplicationResponse>> getByScheme(@PathVariable Long schemeId) {
        return ResponseEntity.ok(applicationService.getApplicationsByScheme(schemeId));
    }

    @PreAuthorize("hasRole('FIELD_OFFICER')")
    @PatchMapping("/{id}/field-verify")
    public ResponseEntity<ApplicationResponse> verifyByFieldOfficer(
            @PathVariable Long id, @Valid @RequestBody NotesRequest request) {
        return ResponseEntity.ok(applicationService.verifyByFieldOfficer(id, request));
    }

    @PreAuthorize("hasRole('DISTRICT_OFFICER')")
    @PatchMapping("/{id}/district-verify")
    public ResponseEntity<ApplicationResponse> verifyByDistrictOfficer(
            @PathVariable Long id, @Valid @RequestBody NotesRequest request) {
        return ResponseEntity.ok(applicationService.verifyByDistrictOfficer(id, request));
    }

    @PreAuthorize("hasRole('FINANCE_APPROVER')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApplicationResponse> approve(
            @PathVariable Long id, @Valid @RequestBody NotesRequest request) {
        return ResponseEntity.ok(applicationService.approveByFinance(id, request));
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> reject(
            @PathVariable Long id, @Valid @RequestBody RejectionRequest request) {
        return ResponseEntity.ok(applicationService.reject(id, request));
    }
}