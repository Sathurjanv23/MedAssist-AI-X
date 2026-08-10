package com.medassist.ai.service;

import com.medassist.ai.model.OllamaModels;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Low-level Ollama API client.
 * Handles HTTP communication with retry logic and timeout handling.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OllamaClient {

    @Qualifier("ollamaWebClient")
    private final WebClient ollamaWebClient;

    @Value("${ai.model.default:llama3.2}")
    private String defaultModel;

    @Value("${ai.model.medical:llama3.2}")
    private String medicalModel;

    @Value("${ai.request.max-retries:2}")
    private int maxRetries;

    /**
     * Send a single prompt and get a response (non-streaming).
     */
    public String generate(String prompt) {
        return generate(prompt, defaultModel);
    }

    public String generate(String prompt, String model) {
        String targetModel = (model != null && !model.isBlank()) ? model : defaultModel;
        OllamaModels.OllamaRequest request = OllamaModels.OllamaRequest.builder()
                .model(targetModel)
                .prompt(prompt)
                .stream(false)
                .options(Map.of("temperature", 0.3, "num_predict", 2048))
                .build();

        try {
            OllamaModels.OllamaResponse response = ollamaWebClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OllamaModels.OllamaResponse.class)
                    .retryWhen(Retry.max(Math.max(0, maxRetries))
                            .filter(ex -> ex instanceof WebClientRequestException))
                    .block();

            if (response != null && response.getResponse() != null) {
                return response.getResponse().trim();
            }
            return getFallbackResponse();

        } catch (WebClientRequestException e) {
            log.error("Ollama connection failed: {}. Is Ollama running?", e.getMessage());
            return getFallbackResponse();
        } catch (Exception e) {
            log.error("Ollama error: {}", e.getMessage());
            return getFallbackResponse();
        }
    }

    /**
     * Chat endpoint — supports conversation history.
     */
    public String chat(List<OllamaModels.OllamaMessage> messages, String model) {
        String targetModel = (model != null && !model.isBlank()) ? model : defaultModel;
        OllamaModels.OllamaRequest request = OllamaModels.OllamaRequest.builder()
                .model(targetModel)
                .messages(messages)
                .stream(false)
                .build();

        try {
            OllamaModels.OllamaResponse response = ollamaWebClient.post()
                    .uri("/api/chat")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OllamaModels.OllamaResponse.class)
                    .retryWhen(Retry.max(Math.max(0, maxRetries))
                            .filter(ex -> ex instanceof WebClientRequestException))
                    .block();

            if (response != null && response.getMessage() != null && response.getMessage().getContent() != null) {
                return response.getMessage().getContent().trim();
            }
            return getFallbackResponse();

        } catch (WebClientRequestException e) {
            log.warn("Ollama connection refused — AI service may not be running: {}", e.getMessage());
            return getFallbackResponse();
        } catch (Exception e) {
            log.error("Chat error: {}", e.getMessage());
            return getFallbackResponse();
        }
    }

    public boolean isAvailable() {
        try {
            ollamaWebClient.get()
                    .uri("/api/tags")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getFallbackResponse() {
        return "I'm currently experiencing technical difficulties. Please try again in a moment. " +
               "For urgent health concerns, please consult a healthcare professional.\n\n" +
               "⚕️ *This is an AI assistant — always consult a doctor for medical decisions.*";
    }
}

