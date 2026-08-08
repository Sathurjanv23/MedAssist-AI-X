package com.medassist.audit.repository;

import com.medassist.audit.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    Page<AuditLog> findByActorIdOrderByCreatedAtDesc(String actorId, Pageable pageable);

    Page<AuditLog> findByTargetUserIdOrderByCreatedAtDesc(String targetUserId, Pageable pageable);

    Page<AuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    long countByCreatedAtAfter(LocalDateTime date);

    long countByAction(String action);
}

