package com.sdms.beneficiary.controller;

import com.sdms.auth.security.AuthenticatedUser;
import com.sdms.beneficiary.dto.BeneficiaryCreateRequest;
import com.sdms.beneficiary.dto.BeneficiaryResponse;
import com.sdms.beneficiary.dto.BeneficiarySelfUpdateRequest;
import com.sdms.beneficiary.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER')")
    @PostMapping
    public ResponseEntity<BeneficiaryResponse> createBeneficiary(@Valid @RequestBody BeneficiaryCreateRequest request){
        BeneficiaryResponse created = beneficiaryService.createBeneficiary(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> getAllBeneficiaries(){
        return ResponseEntity.ok(beneficiaryService.getAllActiveBeneficiaries());
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiaryById(@PathVariable Long id){
        return ResponseEntity.ok(beneficiaryService.getBeneficiaryById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/region/{region}")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiariesByRegion(@PathVariable String region){
        return ResponseEntity.ok(beneficiaryService.getBeneficiariesByRegion(region));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER')")
    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> updateBeneficiary(@PathVariable Long id, @Valid @RequestBody BeneficiaryCreateRequest request){
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateBeneficiary(@PathVariable Long id){
        beneficiaryService.deactivateBeneficiary(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateBeneficiary(@PathVariable Long id){
        beneficiaryService.activateBeneficiary(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('BENEFICIARY')")
    @GetMapping("/me")
    public ResponseEntity<BeneficiaryResponse> getOwnProfile(Authentication authentication) {
        AuthenticatedUser authUser = (AuthenticatedUser) authentication.getDetails();
        return ResponseEntity.ok(beneficiaryService.getOwnProfile(authUser.getBeneficiaryId()));
    }

    @PreAuthorize("hasRole('BENEFICIARY')")
    @PatchMapping("/me")
    public ResponseEntity<BeneficiaryResponse> updateOwnProfile(
            Authentication authentication,
            @Valid @RequestBody BeneficiarySelfUpdateRequest request) {
        AuthenticatedUser authUser = (AuthenticatedUser) authentication.getDetails();
        return ResponseEntity.ok(beneficiaryService.updateOwnProfile(authUser.getBeneficiaryId(), request));
    }
}