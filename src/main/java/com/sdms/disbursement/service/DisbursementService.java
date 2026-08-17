package com.sdms.disbursement.service;

import com.sdms.application.entity.Application;
import com.sdms.application.entity.ApplicationStatus;
import com.sdms.application.repository.ApplicationRepository;
import com.sdms.common.exception.ResourceNotFoundException;
import com.sdms.disbursement.dto.DisbursementReleaseRequest;
import com.sdms.disbursement.dto.DisbursementResponse;
import com.sdms.disbursement.entity.Disbursement;
import com.sdms.disbursement.entity.DisbursementStatus;
import com.sdms.disbursement.repository.DisbursementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisbursementService {

    private final DisbursementRepository disbursementRepository;
    private final ApplicationRepository applicationRepository;

    public List<DisbursementResponse> getAllDisbursements() {
        return disbursementRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<DisbursementResponse> getDisbursementsByApplication(Long applicationId) {
        return disbursementRepository.findByApplicationIdOrderByMilestoneNumberAsc(applicationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Disbursement getRequiredDisbursement(Long id) { // using disbursement id only
        return disbursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disbursement not found with id: " + id));
    }

    public DisbursementResponse release(Long id, DisbursementReleaseRequest request) { // the id passed here is disbursement id
        Disbursement disbursement = getRequiredDisbursement(id);

        // checking if the disbursement is already done or not
        // "already done" check, checked before the generic rule
        if (disbursement.getStatus() == DisbursementStatus.RELEASED) {
            throw new IllegalStateException("This milestone has already been released.");
        }


        if (disbursement.getStatus() == DisbursementStatus.FAILED) {
            throw new IllegalStateException("This milestone previously failed and cannot be released directly. Handle it manually.");
        }

        // sequential gating — same idea as Application's workflow gating
        Integer currentMilestone = disbursement.getMilestoneNumber();
        if (currentMilestone > 1) {
            Disbursement previous = disbursementRepository
                    .findByApplicationIdAndMilestoneNumber(disbursement.getApplication().getId(), currentMilestone - 1)
                    .orElseThrow(() -> new IllegalStateException("Previous milestone record is missing."));

            if (previous.getStatus() != DisbursementStatus.RELEASED) {
                throw new IllegalStateException(
                        "Cannot release milestone " + currentMilestone +
                                " before milestone " + (currentMilestone - 1) + " is released."
                );
            }
        }

        disbursement.setStatus(DisbursementStatus.RELEASED);
        disbursement.setDisbursementDate(LocalDate.now());
        disbursement.setRemarks(request.getRemarks());
        disbursementRepository.save(disbursement);

        checkAndMarkApplicationFullyDisbursed(disbursement.getApplication());

        return mapToResponse(disbursement);
    }

    private void checkAndMarkApplicationFullyDisbursed(Application application) {
        List<Disbursement> allMilestones = disbursementRepository.findByApplicationId(application.getId());

        boolean allReleased = allMilestones.stream()
                .allMatch(d -> d.getStatus() == DisbursementStatus.RELEASED);

        if (allReleased) {
            application.setStatus(ApplicationStatus.DISBURSED);
            applicationRepository.save(application);
        }
    }

    public DisbursementResponse markAsFailed(Long id, DisbursementReleaseRequest request) {
        Disbursement disbursement = getRequiredDisbursement(id);

        if (disbursement.getStatus() == DisbursementStatus.RELEASED) {
            throw new IllegalStateException("Cannot mark a released milestone as failed.");
        }
        if (disbursement.getStatus() == DisbursementStatus.FAILED) {
            throw new IllegalStateException("This milestone is already marked as failed.");
        }

        disbursement.setStatus(DisbursementStatus.FAILED);
        disbursement.setRemarks(request.getRemarks());
        disbursementRepository.save(disbursement);

        return mapToResponse(disbursement);
    }

    public DisbursementResponse retry(Long id) {
        Disbursement disbursement = getRequiredDisbursement(id);

        if (disbursement.getStatus() != DisbursementStatus.FAILED) {
            throw new IllegalStateException("Only failed milestones can be retried.");
        }

        disbursement.setStatus(DisbursementStatus.PENDING);
        disbursement.setRemarks(null);
        disbursementRepository.save(disbursement);

        return mapToResponse(disbursement);
    }

    private DisbursementResponse mapToResponse(Disbursement disbursement) {
        Application application = disbursement.getApplication();
        return new DisbursementResponse(
                disbursement.getId(),
                application.getId(),
                application.getBeneficiary().getFullName(),
                application.getScheme().getName(),
                disbursement.getMilestoneNumber(),
                disbursement.getAmount(),
                disbursement.getStatus(),
                disbursement.getDisbursementDate(),
                disbursement.getRemarks()
        );
    }
}