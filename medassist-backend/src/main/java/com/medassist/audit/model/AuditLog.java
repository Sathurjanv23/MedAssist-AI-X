package com.medassist.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AuditLog â€” immutable record of security and data access events.
 * Used for HIPAA-style audit trail requirements in healthcare systems.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
@CompoundIndex(name = "user_action_idx", def = "{'actor_id': 1, 'action': 1, 'created_at': -1}")
public class AuditLog {

    @Id
    private String id;

    @Indexed
    @Field("actor_id")
    private String actorId;         // who performed the action

    @Field("actor_email")
    private String actorEmail;

    @Field("actor_role")
    private String actorRole;

    @Field("action")
    private String action;
    // LOGIN, LOGOUT, REGISTER, VIEW_PROFILE, UPDATE_PROFILE,
    // UPLOAD_REPORT, VIEW_REPORT, AI_CHAT, DOCTOR_ACCESS_PATIENT,
    // ADMIN_ACTION, PASSWORD_CHANGE, TOKEN_REFRESH

    @Indexed
    @Field("target_user_id")
    private String targetUserId;    // whose data was accessed (can differ from actor)

    @Field("resource")
    private String resource;        // e.g., "/api/reports/123"

    @Field("http_method")
    private String httpMethod;

    @Field("ip_address")
    private String ipAddress;

    @Field("user_agent")
    private String userAgent;

    @Field("success")
    private boolean success;

    @Field("error_message")
    private String errorMessage;

    @Field("metadata")
    private Map<String, Object> metadata;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}

