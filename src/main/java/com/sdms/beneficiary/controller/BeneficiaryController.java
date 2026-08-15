package com.sdms.beneficiary.controller;

import com.sdms.beneficiary.dto.BeneficiaryCreateRequest;
import com.sdms.beneficiary.dto.BeneficiaryResponse;
import com.sdms.beneficiary.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> createBeneficiary(@Valid @RequestBody BeneficiaryCreateRequest request){
        BeneficiaryResponse created = beneficiaryService.createBeneficiary(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> getAllBeneficiaries(){
        return ResponseEntity.ok(beneficiaryService.getAllActiveBeneficiaries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiaryById(@PathVariable Long id){
        return ResponseEntity.ok(beneficiaryService.getBeneficiaryById(id));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiariesByRegion(@PathVariable String region){
        return ResponseEntity.ok(beneficiaryService.getBeneficiariesByRegion(region));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> updateBeneficiary(@PathVariable Long id, @Valid @RequestBody BeneficiaryCreateRequest request){
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateBeneficiary(@PathVariable Long id){
        beneficiaryService.deactivateBeneficiary(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateBeneficiary(@PathVariable Long id){
        beneficiaryService.activateBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
}
