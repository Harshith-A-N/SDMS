package com.sdms.application.service;

import com.sdms.application.dto.*;
import com.sdms.application.entity.*;
import com.sdms.application.repository.ApplicationRepository;
import com.sdms.audit.entity.AuditAction;
import com.sdms.audit.service.AuditService;
import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.beneficiary.repository.BeneficiaryRepository;
import com.sdms.common.exception.ResourceNotFoundException;
import com.sdms.disbursement.entity.*;
import com.sdms.disbursement.repository.DisbursementRepository;
import com.sdms.scheme.entity.Scheme;
import com.sdms.scheme.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final SchemeRepository schemeRepository;
    private final DisbursementRepository disbursementRepository;
    private final AuditService auditService;

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

        if (application.getStatus() == ApplicationStatus.FIELD_VERIFIED) {
            throw new IllegalStateException("Field verification is already done for this application.");
        }
        validateStatus(application, ApplicationStatus.SUBMITTED);

        if (application.getEligibilityScore() == 0) {
            throw new IllegalStateException(
                    "Cannot verify: beneficiary does not meet eligibility criteria for this scheme. Use reject instead.");
        }

        application.setFieldOfficerNotes(request.getNotes());
        application.setStatus(ApplicationStatus.FIELD_VERIFIED);
        Application saved = applicationRepository.save(application);

        auditService.log(AuditAction.APPLICATION_FIELD_VERIFIED, "Application", saved.getId()); // audit log

        return mapToResponse(saved);
    }

    public ApplicationResponse verifyByDistrictOfficer(Long id, NotesRequest request) {
        Application application = getRequiredApplication(id);

        if (application.getStatus() == ApplicationStatus.DISTRICT_VERIFIED) {
            throw new IllegalStateException("District verification is already done for this application.");
        }
        validateStatus(application, ApplicationStatus.FIELD_VERIFIED);

        application.setDistrictOfficerNotes(request.getNotes());
        application.setStatus(ApplicationStatus.DISTRICT_VERIFIED);
        Application saved = applicationRepository.save(application);

        auditService.log(AuditAction.APPLICATION_DISTRICT_VERIFIED, "Application", saved.getId());

        return mapToResponse(saved);
    }

    public ApplicationResponse approveByFinance(Long id, NotesRequest request) {
        Application application = getRequiredApplication(id);

        if (application.getStatus() == ApplicationStatus.APPROVED) {
            throw new IllegalStateException("This application is already approved.");
        }
        validateStatus(application, ApplicationStatus.DISTRICT_VERIFIED);

        application.setFinanceApproverNotes(request.getNotes());
        application.setStatus(ApplicationStatus.APPROVED);
        Application saved = applicationRepository.save(application);

        auditService.log(AuditAction.APPLICATION_APPROVED, "Application", saved.getId());

        createMilestoneDisbursements(saved);  // creating the disbursements automatically here, once after the its status is approved

        return mapToResponse(saved);
    }

    private void createMilestoneDisbursements(Application application) {
        // check if disbursements is already created to this application or not
        List<Disbursement> existing = disbursementRepository.findByApplicationId(application.getId());
        if (!existing.isEmpty()) {
            return;
        }

        BigDecimal grantAmount = application.getScheme().getGrantAmount();

        BigDecimal milestone1Amount = grantAmount
                .multiply(BigDecimal.valueOf(0.40))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal milestone2Amount = grantAmount
                .multiply(BigDecimal.valueOf(0.30))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal milestone3Amount = grantAmount
                .subtract(milestone1Amount)
                .subtract(milestone2Amount);

        disbursementRepository.save(new Disbursement(null, application, 1, milestone1Amount, DisbursementStatus.PENDING, null, null));
        disbursementRepository.save(new Disbursement(null, application, 2, milestone2Amount, DisbursementStatus.PENDING, null, null));
        disbursementRepository.save(new Disbursement(null, application, 3, milestone3Amount, DisbursementStatus.PENDING, null, null));
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
        Application saved = applicationRepository.save(application);

        auditService.log(AuditAction.APPLICATION_REJECTED, "Application", saved.getId());

        return mapToResponse(saved);
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