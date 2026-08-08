package com.medassist.notification.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Real-time notification payload sent via WebSocket to the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationPayload {

    private String type;       // MEDICINE_REMINDER, REPORT_READY, AI_COMPLETE, HEALTH_ALERT
    private String title;
    private String message;
    private String severity;   // INFO, WARNING, SUCCESS, ERROR
    private String actionUrl;  // frontend navigation URL
    private String entityId;   // reportId, medicineId, etc.

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

