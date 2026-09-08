package com.sdms.ai.service;

import com.sdms.ai.entity.DocumentVerificationResult;
import com.sdms.ai.repository.DocumentVerificationResultRepository;
import com.sdms.application.entity.Application;
import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.document.entity.ApplicationDocument;
import com.sdms.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseSummaryService {

    private final GeminiClient geminiClient;
    private final DocumentRepository documentRepository;
    private final DocumentVerificationResultRepository verificationResultRepository;

    public String generateSummary(Application application) {
        String prompt = buildPrompt(application);
        // Text-only call — no image, so imageBytes and mimeType are null.
        return geminiClient.generateContent(prompt, null, null);
    }

    private String buildPrompt(Application application) {
        Beneficiary beneficiary = application.getBeneficiary();
        List<ApplicationDocument> documents = documentRepository.findByApplicationId(application.getId());

        StringBuilder prompt = new StringBuilder();
        prompt.append("Write a short summary (4-6 sentences) for a government officer reviewing this subsidy application. ");
        prompt.append("Plain, factual tone. Point out anything that needs the officer's attention.\n\n");

        prompt.append("Beneficiary: ").append(beneficiary.getFullName()).append("\n");
        prompt.append("Declared annual income: ").append(beneficiary.getAnnualIncome()).append("\n");
        prompt.append("Region: ").append(beneficiary.getRegion()).append("\n");
        prompt.append("Application status: ").append(application.getStatus()).append("\n");
        prompt.append("Eligibility score: ").append(application.getEligibilityScore()).append("\n");

        if (application.getFieldOfficerNotes() != null) {
            prompt.append("Field officer notes: ").append(application.getFieldOfficerNotes()).append("\n");
        }
        if (application.getDistrictOfficerNotes() != null) {
            prompt.append("District officer notes: ").append(application.getDistrictOfficerNotes()).append("\n");
        }
        if (application.getFinanceApproverNotes() != null) {
            prompt.append("Finance approver notes: ").append(application.getFinanceApproverNotes()).append("\n");
        }

        prompt.append("\nDocuments submitted (").append(documents.size()).append("):\n");
        for (ApplicationDocument doc : documents) {
            prompt.append("- ").append(doc.getDocumentType());

            Optional<DocumentVerificationResult> verification =
                    verificationResultRepository.findByDocumentId(doc.getId());

            if (verification.isPresent()) {
                DocumentVerificationResult v = verification.get();
                prompt.append(" [AI check: name match = ").append(v.getNameMatch());
                if (v.getIncomeMatch() != null) {
                    prompt.append(", income match = ").append(v.getIncomeMatch());
                }
                if (v.getReason() != null && !v.getReason().isBlank()) {
                    prompt.append(", note: ").append(v.getReason());
                }
                prompt.append("]");
            } else {
                prompt.append(" [AI check: not yet verified]");
            }
            prompt.append("\n");
        }

        return prompt.toString();
    }
}