package com.sdms.document.controller;

import com.sdms.document.dto.DocumentResponse;
import com.sdms.document.entity.ApplicationDocument;
import com.sdms.document.entity.DocumentType;
import com.sdms.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PreAuthorize("hasAnyRole('FIELD_OFFICER','BENEFICIARY')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam Long applicationId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadDocument(applicationId, documentType, file));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER')")
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(documentService.getDocumentsByApplication(applicationId));
    }

    @PreAuthorize("hasRole('BENEFICIARY')")
    @GetMapping("/my-documents/{applicationId}")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(@PathVariable Long applicationId) {
        return ResponseEntity.ok(documentService.getMyDocuments(applicationId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> downloadDocument(@PathVariable Long id) {
        ApplicationDocument document = documentService.getFileForDownload(id);
        File file = new File(document.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .body(new FileSystemResource(file));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}