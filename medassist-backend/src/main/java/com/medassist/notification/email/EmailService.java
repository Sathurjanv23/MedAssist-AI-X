package com.medassist.notification.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * Email notification service using AWS SES.
 * Sends transactional emails: welcome, password reset, report ready, etc.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email:noreply@medassist.ai}")
    private String fromEmail;

    @Value("${aws.ses.from-name:MedAssist AI X}")
    private String fromName;

    /**
     * Send a welcome email after registration.
     */
    @Async("taskExecutor")
    public void sendWelcomeEmail(String toEmail, String firstName) {
        String subject = "Welcome to MedAssist AI X, " + firstName + "!";
        String htmlBody = buildWelcomeEmailHtml(firstName);
        sendEmail(toEmail, subject, htmlBody);
    }

    /**
     * Send password reset email.
     */
    @Async("taskExecutor")
    public void sendPasswordResetEmail(String toEmail, String firstName, String resetToken) {
        String subject = "Reset Your MedAssist AI X Password";
        String resetLink = "https://medassist.ai/reset-password?token=" + resetToken;
        String htmlBody = buildPasswordResetHtml(firstName, resetLink);
        sendEmail(toEmail, subject, htmlBody);
    }

    /**
     * Notify user that their medical report analysis is complete.
     */
    @Async("taskExecutor")
    public void sendReportReadyEmail(String toEmail, String firstName, String reportId) {
        String subject = "Your Medical Report Analysis is Ready";
        String reportLink = "https://medassist.ai/reports/" + reportId;
        String htmlBody = buildReportReadyHtml(firstName, reportLink);
        sendEmail(toEmail, subject, htmlBody);
    }

    // â”€â”€ Internal â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void sendEmail(String toEmail, String subject, String htmlBody) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromName + " <" + fromEmail + ">")
                    .destination(Destination.builder().toAddresses(toEmail).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).charset("UTF-8").build())
                            .body(Body.builder()
                                    .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                                    .build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            log.info("Email sent to: {} | Subject: {}", toEmail, subject);

        } catch (SesException e) {
            log.error("SES email failed for {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Email send failed: {}", e.getMessage());
        }
    }

    private String buildWelcomeEmailHtml(String firstName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background: #f4f4f5; padding: 20px;">
              <div style="max-width:600px;margin:auto;background:white;border-radius:12px;padding:30px;">
                <h1 style="color:#7c3aed;">Welcome to MedAssist AI X! ðŸ¥</h1>
                <p>Hi <strong>%s</strong>,</p>
                <p>Your personal AI healthcare operating system is ready. You can now:</p>
                <ul>
                  <li>Upload and analyze medical reports with AI</li>
                  <li>Track your medications and get reminders</li>
                  <li>Chat with your AI health assistant</li>
                  <li>Monitor your AI Health Twin</li>
                </ul>
                <a href="https://medassist.ai/dashboard"
                   style="background:#7c3aed;color:white;padding:12px 24px;border-radius:8px;text-decoration:none;">
                  Get Started â†’
                </a>
                <p style="color:#6b7280;margin-top:20px;font-size:12px;">
                  âš•ï¸ MedAssist AI X provides health information, not medical advice. Always consult a doctor.
                </p>
              </div>
            </body>
            </html>
            """.formatted(firstName);
    }

    private String buildPasswordResetHtml(String firstName, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background: #f4f4f5; padding: 20px;">
              <div style="max-width:600px;margin:auto;background:white;border-radius:12px;padding:30px;">
                <h2 style="color:#7c3aed;">Password Reset Request</h2>
                <p>Hi <strong>%s</strong>, click the button below to reset your password.</p>
                <p>This link expires in <strong>1 hour</strong>.</p>
                <a href="%s" style="background:#7c3aed;color:white;padding:12px 24px;border-radius:8px;text-decoration:none;">
                  Reset Password
                </a>
                <p style="color:#6b7280;font-size:12px;margin-top:20px;">
                  If you didn't request this, ignore this email.
                </p>
              </div>
            </body>
            </html>
            """.formatted(firstName, resetLink);
    }

    private String buildReportReadyHtml(String firstName, String reportLink) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background: #f4f4f5; padding: 20px;">
              <div style="max-width:600px;margin:auto;background:white;border-radius:12px;padding:30px;">
                <h2 style="color:#7c3aed;">âœ… Your Report is Ready</h2>
                <p>Hi <strong>%s</strong>, your medical report has been analyzed by MedAssist AI.</p>
                <a href="%s" style="background:#7c3aed;color:white;padding:12px 24px;border-radius:8px;text-decoration:none;">
                  View Report Analysis â†’
                </a>
                <p style="color:#6b7280;font-size:12px;margin-top:20px;">
                  âš•ï¸ AI analysis is for informational purposes only. Please consult your doctor.
                </p>
              </div>
            </body>
            </html>
            """.formatted(firstName, reportLink);
    }
}

