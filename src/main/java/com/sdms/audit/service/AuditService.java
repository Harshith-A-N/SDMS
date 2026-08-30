package com.sdms.audit.service;

import com.sdms.audit.entity.AuditAction;
import com.sdms.audit.entity.AuditLog;
import com.sdms.audit.repository.AuditLogRepository;
import com.sdms.auth.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(AuditAction action, String entityType, Long entityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN")
                .replace("ROLE_", "");

        AuthenticatedUser authUser = (AuthenticatedUser) auth.getDetails();
        Long userId = authUser.getUserId();

        AuditLog logEntry = new AuditLog(
                null,
                userId,
                username,
                role,
                action,
                entityType,
                entityId,
                LocalDateTime.now()
        );

        auditLogRepository.save(logEntry);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }
}