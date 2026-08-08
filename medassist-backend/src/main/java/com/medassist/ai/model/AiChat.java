package com.medassist.ai.model;

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
import java.util.ArrayList;
import java.util.List;

/**
 * AiChat â€” stores conversation sessions between user and AI health assistant.
 * Uses embedded messages pattern for efficient conversation retrieval.
 * Conversation history is capped at 50 messages per session for token efficiency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_chats")
@CompoundIndex(name = "user_created_idx", def = "{'user_id': 1, 'created_at': -1}")
public class AiChat {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("session_title")
    private String sessionTitle;

    @Field("messages")
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @Field("context")
    private ChatContext context;

    @Field("is_active")
    @Builder.Default
    private boolean active = true;

    @Field("message_count")
    @Builder.Default
    private int messageCount = 0;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("last_message_at")
    private LocalDateTime lastMessageAt;

    // â”€â”€ Embedded Message â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;       // user, assistant
        private String content;
        private LocalDateTime timestamp;
        private String model;      // which AI model responded
        private Boolean flagged;   // safety flag
    }

    // â”€â”€ Embedded Context â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatContext {
        private String reportId;      // linked report if any
        private String language;      // en, ta, si
        private List<String> topics;  // health topics discussed
        private String userHealthSummary; // brief health context for AI
    }
}

