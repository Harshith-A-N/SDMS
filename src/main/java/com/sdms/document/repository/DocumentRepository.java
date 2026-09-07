package com.sdms.document.repository;

import com.sdms.document.entity.ApplicationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<ApplicationDocument, Long> {
    List<ApplicationDocument> findByApplicationId(Long applicationId);
}