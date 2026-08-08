package com.medassist.ai.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Ollama API request/response DTOs.
 */
public class OllamaModels {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OllamaRequest {
        private String model;
        private String prompt;
        private Boolean stream;
        private Map<String, Object> options;
        private String system;
        private List<OllamaMessage> messages; // for /api/chat endpoint
        private String format;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaMessage {
        private String role;    // system, user, assistant
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaResponse {
        private String model;
        private String response;   // for /api/generate
        private OllamaMessage message; // for /api/chat
        private Boolean done;
        private Long totalDuration;
        private Long promptEvalCount;
        private Long evalCount;
    }
}

