package com.medassist.audit.service;

import com.medassist.audit.model.AuditLog;
import com.medassist.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Audit Service â€” records immutable HIPAA-style audit events asynchronously.
 * Called by controllers and security components.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Asynchronously log an audit event â€” fire-and-forget.
     */
    @Async("taskExecutor")
    public void log(String actorId, String actorEmail, String actorRole,
                    String action, String targetUserId, String resource,
                    String httpMethod, String ipAddress, String userAgent,
                    boolean success, String errorMessage, Map<String, Object> metadata) {
        try {
            AuditLog entry = AuditLog.builder()
                    .actorId(actorId)
                    .actorEmail(actorEmail)
                    .actorRole(actorRole)
                    .action(action)
                    .targetUserId(targetUserId)
                    .resource(resource)
                    .httpMethod(httpMethod)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .success(success)
                    .errorMessage(errorMessage)
                    .metadata(metadata)
                    .createdAt(LocalDateTime.now())
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * Convenience method for simple action logging.
     */
    @Async("taskExecutor")
    public void logAction(String actorId, String actorEmail, String action, boolean success) {
        log(actorId, actorEmail, null, action, null, null, null, null, null, success, null, null);
    }
}

