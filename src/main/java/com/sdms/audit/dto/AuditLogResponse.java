package com.sdms.audit.dto;

import com.sdms.audit.entity.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long performedByUserId;
    private String performedByUsername;
    private String role;
    private AuditAction action;
    private String entityType;
    private Long entityId;
    private LocalDateTime timestamp;
}