package com.sdms.beneficiary.service;

import com.sdms.audit.entity.AuditAction;
import com.sdms.audit.service.AuditService;
import com.sdms.beneficiary.dto.BeneficiaryCreateRequest;
import com.sdms.beneficiary.dto.BeneficiaryResponse;
import com.sdms.beneficiary.dto.BeneficiarySelfUpdateRequest;
import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.beneficiary.repository.BeneficiaryRepository;
import com.sdms.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AuditService auditService;

    public BeneficiaryResponse createBeneficiary(BeneficiaryCreateRequest request) {
        beneficiaryRepository.findByAadhaarNumber(request.getAadhaarNumber())
                .ifPresent(b -> {
                    throw new IllegalStateException("Beneficiary already registered with this Aadhaar number");
                });

        validateMinimumAge(request.getDateOfBirth());

        Beneficiary beneficiary = mapToEntity(request);
        Beneficiary saved = beneficiaryRepository.save(beneficiary);

        auditService.log(AuditAction.BENEFICIARY_CREATED, "Beneficiary", saved.getId());

        return mapToResponse(saved);
    }

    private void validateMinimumAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalStateException("Beneficiary must be at least 18 years old");
        }
    }

    public List<BeneficiaryResponse> getAllActiveBeneficiaries(){
        return beneficiaryRepository.findByIsActiveTrue().stream().map(this::mapToResponse).toList();
    }

    public BeneficiaryResponse getBeneficiaryById(Long id){
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + id));
        return mapToResponse(beneficiary);
    }

    public List<BeneficiaryResponse> getBeneficiariesByRegion(String region) {
        return beneficiaryRepository.findByRegion(region)
                .stream().map(this::mapToResponse).toList();
    }

    public BeneficiaryResponse updateBeneficiary(Long id, BeneficiaryCreateRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + id));

        beneficiaryRepository.findByAadhaarNumber(request.getAadhaarNumber())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Another beneficiary already exists with this Aadhaar number");
                });

        validateMinimumAge(request.getDateOfBirth());

        beneficiary.setFullName(request.getFullName());
        beneficiary.setAadhaarNumber(request.getAadhaarNumber());
        beneficiary.setDateOfBirth(request.getDateOfBirth());
        beneficiary.setGender(request.getGender());
        beneficiary.setPhoneNumber(request.getPhoneNumber());
        beneficiary.setEmail(request.getEmail());
        beneficiary.setAddress(request.getAddress());
        beneficiary.setRegion(request.getRegion());
        beneficiary.setAnnualIncome(request.getAnnualIncome());
        beneficiary.setBankAccountNumber(request.getBankAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());

        Beneficiary saved = beneficiaryRepository.save(beneficiary);

        auditService.log(AuditAction.BENEFICIARY_UPDATED, "Beneficiary", saved.getId());

        return mapToResponse(saved);
    }

    public void deactivateBeneficiary(Long id){
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + id));
        beneficiary.setIsActive(false);
        beneficiaryRepository.save(beneficiary);

        auditService.log(AuditAction.BENEFICIARY_DEACTIVATED, "Beneficiary", beneficiary.getId());
    }

    public void activateBeneficiary(Long id){
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + id));
        beneficiary.setIsActive(true);
        beneficiaryRepository.save(beneficiary);

        auditService.log(AuditAction.BENEFICIARY_ACTIVATED, "Beneficiary", beneficiary.getId());
    }

    public BeneficiaryResponse getOwnProfile(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + beneficiaryId));
        return mapToResponse(beneficiary);
    }

    public BeneficiaryResponse updateOwnProfile(Long beneficiaryId, BeneficiarySelfUpdateRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + beneficiaryId));

        beneficiary.setPhoneNumber(request.getPhoneNumber());
        beneficiary.setEmail(request.getEmail());
        beneficiary.setAddress(request.getAddress());
        beneficiary.setBankAccountNumber(request.getBankAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return mapToResponse(saved);
    }

    private Beneficiary mapToEntity(BeneficiaryCreateRequest request){
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setFullName(request.getFullName());
        beneficiary.setAadhaarNumber(request.getAadhaarNumber());
        beneficiary.setDateOfBirth(request.getDateOfBirth());
        beneficiary.setGender(request.getGender());
        beneficiary.setPhoneNumber(request.getPhoneNumber());
        beneficiary.setEmail(request.getEmail());
        beneficiary.setAddress(request.getAddress());
        beneficiary.setRegion(request.getRegion());
        beneficiary.setAnnualIncome(request.getAnnualIncome());
        beneficiary.setBankAccountNumber(request.getBankAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());
        return beneficiary;
    }

    private BeneficiaryResponse mapToResponse(Beneficiary beneficiary){
        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getFullName(),
                beneficiary.getAadhaarNumber(),
                beneficiary.getDateOfBirth(),
                beneficiary.getGender(),
                beneficiary.getPhoneNumber(),
                beneficiary.getEmail(),
                beneficiary.getAddress(),
                beneficiary.getRegion(),
                beneficiary.getAnnualIncome(),
                beneficiary.getBankAccountNumber(),
                beneficiary.getIfscCode(),
                beneficiary.getIsActive()
        );
    }
}