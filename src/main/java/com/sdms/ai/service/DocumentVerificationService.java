package com.sdms.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdms.ai.entity.DocumentVerificationResult;
import com.sdms.ai.repository.DocumentVerificationResultRepository;
import com.sdms.application.entity.Application;
import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.document.entity.ApplicationDocument;
import com.sdms.document.entity.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentVerificationService {

    private final GeminiClient geminiClient;
    private final DocumentVerificationResultRepository verificationResultRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Runs AI verification on a single uploaded document and saves the result.
     * Called automatically right after a document is uploaded.
     * On any failure (API down, bad response, etc.), this logs the error and
     * returns quietly instead of throwing — a failed AI check should never
     * block or break the actual document upload.
     */
    public void verifyDocument(ApplicationDocument document) {
        try {
            byte[] imageBytes = Files.readAllBytes(Path.of(document.getFilePath()));
            String mimeType = resolveMimeType(document.getOriginalFileName());

            Application application = document.getApplication();
            Beneficiary beneficiary = application.getBeneficiary();

            String prompt = buildPrompt(document.getDocumentType(), beneficiary);

            String rawReply = geminiClient.generateContent(prompt, imageBytes, mimeType);
            JsonNode parsed = parseJsonReply(rawReply);

            DocumentVerificationResult result = new DocumentVerificationResult();
            result.setDocument(document);
            result.setNameMatch(parsed.get("nameMatch").asBoolean());
            result.setIncomeMatch(parsed.get("incomeMatch").isNull() ? null : parsed.get("incomeMatch").asBoolean());
            result.setReason(parsed.get("reason").asText());
            result.setVerifiedAt(LocalDateTime.now());

            verificationResultRepository.save(result);

        } catch (IOException e) {
            log.error("AI verification failed to read file for document id={}: {}", document.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("AI verification failed for document id={}: {}", document.getId(), e.getMessage());
        }
    }

    private String buildPrompt(DocumentType documentType, Beneficiary beneficiary) {
        boolean isIncomeCertificate = documentType == DocumentType.INCOME_CERTIFICATE;

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are verifying a government subsidy applicant's document. ");
        prompt.append("Registered name on file: \"").append(beneficiary.getFullName()).append("\". ");

        if (isIncomeCertificate) {
            prompt.append("Registered annual income on file: ").append(beneficiary.getAnnualIncome()).append(". ");
        }

        prompt.append("Look at the attached document image and do the following:\n");
        prompt.append("1. Read the name printed on the document and check if it reasonably matches the registered name (allow minor spelling/formatting differences, but flag if clearly a different person).\n");

        if (isIncomeCertificate) {
            prompt.append("2. Read the income figure on the document and check if it roughly matches the registered annual income (allow reasonable variation, flag if very different).\n");
        }

        prompt.append("Respond with ONLY raw JSON, no markdown formatting, no extra text, in exactly this shape:\n");
        prompt.append("{\"nameMatch\": true or false, \"incomeMatch\": true or false or null, \"reason\": \"one short sentence, empty string if no issues\"}\n");
        if (!isIncomeCertificate) {
            prompt.append("Since this is not an income certificate, incomeMatch must be null.");
        }

        return prompt.toString();
    }

    private JsonNode parseJsonReply(String rawReply) throws IOException {
        String cleaned = rawReply.trim()
                .replaceAll("^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();
        return objectMapper.readTree(cleaned);
    }

    private String resolveMimeType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".pdf")) return "application/pdf";
        return "image/jpeg"; // covers .jpg and .jpeg
    }
}