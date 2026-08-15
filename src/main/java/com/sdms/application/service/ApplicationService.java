package com.sdms.application.service;

import com.sdms.application.dto.ApplicationCreateRequest;
import com.sdms.application.dto.ApplicationResponse;
import com.sdms.application.dto.NotesRequest;
import com.sdms.application.dto.RejectionRequest;
import com.sdms.application.entity.Application;
import com.sdms.application.entity.ApplicationStatus;
import com.sdms.application.repository.ApplicationRepository;
import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.beneficiary.repository.BeneficiaryRepository;
import com.sdms.common.exception.ResourceNotFoundException;
import com.sdms.scheme.entity.Scheme;
import com.sdms.scheme.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final SchemeRepository schemeRepository;

    public ApplicationResponse createApplication(ApplicationCreateRequest request) {

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Beneficiary not found with id: " + request.getBeneficiaryId()));

        Scheme scheme = schemeRepository.findById(request.getSchemeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scheme not found with id: " + request.getSchemeId()));

        // checking for re-apply
        applicationRepository.findByBeneficiaryIdAndSchemeIdAndIsActiveTrueAndStatusNot(
                        request.getBeneficiaryId(), request.getSchemeId(), ApplicationStatus.REJECTED)
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "An active application already exists for this beneficiary and scheme");
                });

        Application application = new Application();
        application.setBeneficiary(beneficiary);
        application.setScheme(scheme);
        application.setApplicationDate(LocalDate.now());
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setEligibilityScore(calculateEligibilityScore(beneficiary, scheme));
        application.setActive(true);

        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        return mapToResponse(application);
    }

    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByBeneficiary(Long beneficiaryId) {
        return applicationRepository.findByBeneficiaryId(beneficiaryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByScheme(Long schemeId) {
        return applicationRepository.findBySchemeId(schemeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // workflow methods
    public ApplicationResponse verifyByFieldOfficer(Long id, NotesRequest request) {
        Application application = getRequiredApplication(id);
        validateStatus(application, ApplicationStatus.SUBMITTED);
        application.setFieldOfficerNotes(request.getNotes());
        application.setStatus(ApplicationStatus.FIELD_VERIFIED);
        return mapToResponse(applicationRepository.save(application));
    }

    public ApplicationResponse verifyByDistrictOfficer(Long id, NotesRequest request) {
        Application application = getRequiredApplication(id);
        validateStatus(application, ApplicationStatus.FIELD_VERIFIED);
        application.setDistrictOfficerNotes(request.getNotes());
        application.setStatus(ApplicationStatus.DISTRICT_VERIFIED);
        return mapToResponse(applicationRepository.save(application));
    }

    public ApplicationResponse approveByFinance(Long id, NotesRequest request) {
        Application application = getRequiredApplication(id);
        validateStatus(application, ApplicationStatus.DISTRICT_VERIFIED);
        application.setFinanceApproverNotes(request.getNotes());
        application.setStatus(ApplicationStatus.APPROVED);
        return mapToResponse(applicationRepository.save(application));
    }

    public ApplicationResponse disburse(Long id) {
        Application application = getRequiredApplication(id);
        validateStatus(application, ApplicationStatus.APPROVED);
        application.setStatus(ApplicationStatus.DISBURSED);
        return mapToResponse(applicationRepository.save(application));
    }

    public ApplicationResponse reject(Long id, RejectionRequest request) {
        Application application = getRequiredApplication(id);
        if (application.getStatus() == ApplicationStatus.DISBURSED
                || application.getStatus() == ApplicationStatus.REJECTED) {
            throw new IllegalStateException(
                    "Cannot reject an application that is already " + application.getStatus());
        }
        application.setRejectedBy(request.getRejectedBy());
        application.setRejectionReason(request.getRejectionReason());
        application.setStatus(ApplicationStatus.REJECTED);
        return mapToResponse(applicationRepository.save(application));
    }

    // ---- helpers ----
    private Application getRequiredApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    private void validateStatus(Application application, ApplicationStatus expected) {
        if (application.getStatus() != expected) {
            throw new IllegalStateException(
                    "Application must be in " + expected + " status, but is currently " + application.getStatus());
        }
    }

    // ---- Eligibility scoring ----
    private Integer calculateEligibilityScore(Beneficiary beneficiary, Scheme scheme) {
        boolean withinMin = beneficiary.getAnnualIncome().compareTo(scheme.getMinIncome()) >= 0;
        boolean withinMax = beneficiary.getAnnualIncome().compareTo(scheme.getMaxIncome()) <= 0;

        if (withinMin && withinMax) {
            return 100;
        }
        return 0;
    }   

    // ---- mapping helpers ----
    private ApplicationResponse mapToResponse(Application app) {
        return new ApplicationResponse(
                app.getId(),
                app.getBeneficiary().getId(),
                app.getBeneficiary().getFullName(),
                app.getScheme().getId(),
                app.getScheme().getName(),
                app.getApplicationDate(),
                app.getStatus(),
                app.getEligibilityScore(),
                app.getFieldOfficerNotes(),
                app.getDistrictOfficerNotes(),
                app.getFinanceApproverNotes(),
                app.getRejectedBy(),
                app.getRejectionReason(),
                app.isActive()
        );
    }
}