package com.medassist.notification.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket notification service â€” sends real-time events to connected frontend clients.
 *
 * <p>Frontend subscribes to:
 *   /user/{userId}/queue/notifications â€” user-specific (SimpMessagingTemplate.convertAndSendToUser)
 *   /topic/ai-progress/{reportId}     â€” report processing progress
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a notification to a specific user.
     */
    public void sendToUser(String userId, NotificationPayload payload) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                payload
            );
            log.debug("WS notification sent to user {}: {}", userId, payload.getType());
        } catch (Exception e) {
            log.error("Failed to send WS notification to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Send a report processing progress update.
     */
    public void sendReportProgress(String reportId, String status, int progressPercent) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("REPORT_PROGRESS")
                .title("Report Analysis")
                .message(status)
                .severity("INFO")
                .entityId(reportId)
                .build();
        messagingTemplate.convertAndSend("/topic/ai-progress/" + reportId, payload);
    }

    /**
     * Notify user that their report analysis is complete.
     */
    public void sendReportComplete(String userId, String reportId, boolean success) {
        NotificationPayload payload = NotificationPayload.builder()
                .type(success ? "REPORT_READY" : "REPORT_FAILED")
                .title(success ? "Report Analysis Complete" : "Report Analysis Failed")
                .message(success
                    ? "Your medical report has been analyzed. View results now."
                    : "Report analysis failed. Please try again.")
                .severity(success ? "SUCCESS" : "ERROR")
                .actionUrl("/reports/" + reportId)
                .entityId(reportId)
                .build();
        sendToUser(userId, payload);
    }

    /**
     * Send a medicine reminder notification.
     */
    public void sendMedicineReminder(String userId, String medicineName, String dosage) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("MEDICINE_REMINDER")
                .title("Medicine Reminder")
                .message("Time to take " + medicineName + " â€” " + dosage)
                .severity("INFO")
                .actionUrl("/medicines")
                .build();
        sendToUser(userId, payload);
    }

    /**
     * Send a health alert notification.
     */
    public void sendHealthAlert(String userId, String alertTitle, String message) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("HEALTH_ALERT")
                .title(alertTitle)
                .message(message)
                .severity("WARNING")
                .actionUrl("/health-twin")
                .build();
        sendToUser(userId, payload);
    }
}

