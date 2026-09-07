package com.sdms.document.dto;

import com.sdms.document.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private Long applicationId;
    private DocumentType documentType;
    private String originalFileName;
    private Long fileSize;
    private String uploadedByUsername;
    private LocalDateTime uploadedAt;
}