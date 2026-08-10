package com.medassist.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OllamaMessage {
        private String role;    // system, user, assistant
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OllamaResponse {
        private String model;
        private String response;   // for /api/generate
        private OllamaMessage message; // for /api/chat
        private Boolean done;

        @JsonProperty("done_reason")
        private String doneReason;

        @JsonProperty("total_duration")
        private Long totalDuration;

        @JsonProperty("prompt_eval_count")
        private Long promptEvalCount;

        @JsonProperty("eval_count")
        private Long evalCount;
    }
}


