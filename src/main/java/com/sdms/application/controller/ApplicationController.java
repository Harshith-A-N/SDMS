package com.sdms.application.controller;

import com.sdms.application.dto.*;
import com.sdms.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @Valid @RequestBody ApplicationCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    public ResponseEntity<List<ApplicationResponse>> getByBeneficiary(@PathVariable Long beneficiaryId) {
        return ResponseEntity.ok(applicationService.getApplicationsByBeneficiary(beneficiaryId));
    }

    @GetMapping("/scheme/{schemeId}")
    public ResponseEntity<List<ApplicationResponse>> getByScheme(@PathVariable Long schemeId) {
        return ResponseEntity.ok(applicationService.getApplicationsByScheme(schemeId));
    }

    @PatchMapping("/{id}/field-verify")
    public ResponseEntity<ApplicationResponse> verifyByFieldOfficer(
            @PathVariable Long id, @Valid @RequestBody NotesRequest request) {
        return ResponseEntity.ok(applicationService.verifyByFieldOfficer(id, request));
    }

    @PatchMapping("/{id}/district-verify")
    public ResponseEntity<ApplicationResponse> verifyByDistrictOfficer(
            @PathVariable Long id, @Valid @RequestBody NotesRequest request) {
        return ResponseEntity.ok(applicationService.verifyByDistrictOfficer(id, request));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApplicationResponse> approve(
            @PathVariable Long id, @Valid @RequestBody NotesRequest request) {
        return ResponseEntity.ok(applicationService.approveByFinance(id, request));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> reject(
            @PathVariable Long id, @Valid @RequestBody RejectionRequest request) {
        return ResponseEntity.ok(applicationService.reject(id, request));
    }
}