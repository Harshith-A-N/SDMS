package com.sdms.audit.controller;

import com.sdms.audit.dto.AuditLogResponse;
import com.sdms.audit.entity.AuditLog;
import com.sdms.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs().stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsForEntity(
            @PathVariable String entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(
                auditService.getLogsForEntity(entityType, entityId).stream().map(this::mapToResponse).toList());
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getPerformedByUserId(),
                log.getPerformedByUsername(),
                log.getRole(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getTimestamp()
        );
    }
}