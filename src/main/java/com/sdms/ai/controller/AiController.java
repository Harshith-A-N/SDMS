package com.sdms.ai.controller;

import com.sdms.ai.dto.CaseSummaryResponse;
import com.sdms.ai.dto.VerificationResponse;
import com.sdms.ai.entity.DocumentVerificationResult;
import com.sdms.ai.repository.DocumentVerificationResultRepository;
import com.sdms.ai.service.CaseSummaryService;
import com.sdms.application.entity.Application;
import com.sdms.application.repository.ApplicationRepository;
import com.sdms.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final DocumentVerificationResultRepository verificationResultRepository;
    private final CaseSummaryService caseSummaryService;
    private final ApplicationRepository applicationRepository;

    @GetMapping("/verification/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER')")
    public VerificationResponse getVerificationResult(@PathVariable Long documentId) {
        DocumentVerificationResult result = verificationResultRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("No AI verification result found for this document yet"));

        return new VerificationResponse(
                documentId,
                result.getNameMatch(),
                result.getIncomeMatch(),
                result.getReason(),
                result.getVerifiedAt()
        );
    }

    @GetMapping("/case-summary/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER')")
    public CaseSummaryResponse getCaseSummary(@PathVariable Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        String summary = caseSummaryService.generateSummary(application);
        return new CaseSummaryResponse(applicationId, summary);
    }
}