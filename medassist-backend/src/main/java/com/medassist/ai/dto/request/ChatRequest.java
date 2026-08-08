package com.medassist.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI Chat request DTO â€” message sent by user to AI assistant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 4000, message = "Message must not exceed 4000 characters")
    private String message;

    private String sessionId;    // existing session ID to continue; null = new session
    private String reportId;     // optional: link to a specific report for context
    private String language;     // en, ta, si â€” overrides user preference if set
}

