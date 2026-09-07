package com.sdms.document.service;

import com.sdms.application.entity.Application;
import com.sdms.application.repository.ApplicationRepository;
import com.sdms.auth.security.AuthenticatedUser;
import com.sdms.common.exception.ResourceNotFoundException;
import com.sdms.document.dto.DocumentResponse;
import com.sdms.document.entity.ApplicationDocument;
import com.sdms.document.entity.DocumentType;
import com.sdms.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;

    @Value("${document.upload-dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png"
    );

    public DocumentResponse uploadDocument(Long applicationId, DocumentType documentType, MultipartFile file) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        AuthenticatedUser authUser = getCurrentAuthUser();
        String role = getCurrentRole();

        if (role.equals("BENEFICIARY")) {
            Long ownBeneficiaryId = authUser.getBeneficiaryId();
            if (!application.getBeneficiary().getId().equals(ownBeneficiaryId)) {
                throw new ResourceNotFoundException("Application not found with id: " + applicationId);
            }
        }

        validateFile(file);

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path folderPath = Paths.get(uploadDir, String.valueOf(applicationId));
        Path filePath = folderPath.resolve(storedFileName);

        try {
            Files.createDirectories(folderPath);
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }

        ApplicationDocument document = new ApplicationDocument(
                null,
                application,
                documentType,
                file.getOriginalFilename(),
                storedFileName,
                filePath.toString(),
                file.getSize(),
                authUser.getUserId(),
                getCurrentUsername(),
                LocalDateTime.now()
        );

        ApplicationDocument saved = documentRepository.save(document);
        return mapToResponse(saved);
    }

    public List<DocumentResponse> getDocumentsByApplication(Long applicationId) {
        return documentRepository.findByApplicationId(applicationId)
                .stream().map(this::mapToResponse).toList();
    }

    public List<DocumentResponse> getMyDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        AuthenticatedUser authUser = getCurrentAuthUser();
        if (!application.getBeneficiary().getId().equals(authUser.getBeneficiaryId())) {
            throw new ResourceNotFoundException("Application not found with id: " + applicationId);
        }

        return getDocumentsByApplication(applicationId);
    }

    public ApplicationDocument getFileForDownload(Long documentId) {
        ApplicationDocument document = getRequiredDocument(documentId);

        String role = getCurrentRole();
        if (role.equals("BENEFICIARY")) {
            checkOwnership(document);
        }

        return document;
    }

    public void deleteDocument(Long documentId) {
        ApplicationDocument document = getRequiredDocument(documentId);

        String role = getCurrentRole();
        if (role.equals("BENEFICIARY")) {
            checkOwnership(document);
        }

        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from disk: " + e.getMessage());
        }

        documentRepository.delete(document);
    }

    private void checkOwnership(ApplicationDocument document) {
        AuthenticatedUser authUser = getCurrentAuthUser();
        Long ownerBeneficiaryId = document.getApplication().getBeneficiary().getId();
        if (!ownerBeneficiaryId.equals(authUser.getBeneficiaryId())) {
            throw new ResourceNotFoundException("Document not found with id: " + document.getId());
        }
    }

    private ApplicationDocument getRequiredDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("File is empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalStateException("File exceeds maximum size of 5MB.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalStateException("Only PDF, JPG, and PNG files are allowed.");
        }
    }

    private AuthenticatedUser getCurrentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (AuthenticatedUser) authentication.getDetails();
    }

    private String getCurrentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private DocumentResponse mapToResponse(ApplicationDocument doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getApplication().getId(),
                doc.getDocumentType(),
                doc.getOriginalFileName(),
                doc.getFileSize(),
                doc.getUploadedByUsername(),
                doc.getUploadedAt()
        );
    }
}