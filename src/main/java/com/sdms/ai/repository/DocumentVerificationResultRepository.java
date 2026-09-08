package com.sdms.ai.repository;

import com.sdms.ai.entity.DocumentVerificationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentVerificationResultRepository extends JpaRepository<DocumentVerificationResult, Long> {

    Optional<DocumentVerificationResult> findByDocumentId(Long documentId);
}