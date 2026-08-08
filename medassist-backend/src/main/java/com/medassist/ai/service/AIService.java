package com.medassist.ai.service;

import com.medassist.common.constants.AppConstants;
import com.medassist.ai.dto.request.ChatRequest;
import com.medassist.ai.dto.request.SymptomRequest;
import com.medassist.ai.dto.response.ChatResponse;
import com.medassist.ai.model.AiChat;
import com.medassist.ai.model.OllamaModels;
import com.medassist.report.model.MedicalReport;
import com.medassist.ai.repository.ChatRepository;
import com.medassist.report.repository.MedicalReportRepository;
import com.medassist.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Core AI service â€” orchestrates chat, report analysis, and symptom checking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final OllamaClient ollamaClient;
    private final ContextBuilder contextBuilder;
    private final SafetyGuard safetyGuard;
    private final ChatRepository chatRepository;
    private final MedicalReportRepository reportRepository;
    private final UserRepository userRepository;

    @Value("${ai.model.default:llama3.2}")
    private String defaultModel;

    // â”€â”€ AI Chat â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public ChatResponse chat(String userId, ChatRequest request) {
        // Safety check
        if (safetyGuard.isHarmfulRequest(request.getMessage())) {
            log.warn("Blocked harmful request from user: {}", userId);
            return ChatResponse.builder()
                    .role("assistant")
                    .message(safetyGuard.getBlockedResponse())
                    .flagged(true)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // Get or create chat session
        AiChat session = getOrCreateSession(userId, request.getSessionId());

        // Build context
        String userContext = contextBuilder.buildUserContext(userId);
        String language = request.getLanguage() != null ? request.getLanguage() : getUserLanguage(userId);
        String systemPrompt = contextBuilder.buildSystemPrompt(userContext, language);

        // Build message history for AI
        List<OllamaModels.OllamaMessage> messages = new ArrayList<>();
        messages.add(OllamaModels.OllamaMessage.builder()
                .role("system").content(systemPrompt).build());
        messages.addAll(contextBuilder.buildChatHistory(session, AppConstants.AI_MAX_CONVERSATION_HISTORY));
        messages.add(OllamaModels.OllamaMessage.builder()
                .role("user").content(request.getMessage()).build());

        // Call AI
        String aiResponse = ollamaClient.chat(messages, defaultModel);
        aiResponse = safetyGuard.processResponse(aiResponse);

        // Save to session
        addMessageToSession(session, "user", request.getMessage(), null);
        addMessageToSession(session, "assistant", aiResponse, defaultModel);
        chatRepository.save(session);

        return ChatResponse.builder()
                .sessionId(session.getId())
                .message(aiResponse)
                .role("assistant")
                .model(defaultModel)
                .flagged(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // â”€â”€ Medical Report Analysis â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Async("aiExecutor")
    public CompletableFuture<MedicalReport.AnalysisResult> analyzeReport(
            String extractedText, String userId) {

        String userContext = contextBuilder.buildUserContext(userId);

        String prompt = """
            You are a medical AI assistant analyzing a patient's medical report.
            
            PATIENT CONTEXT:
            %s
            
            MEDICAL REPORT TEXT:
            %s
            
            Please analyze this report and provide a structured response in the following JSON format:
            {
              "summary": "Brief 2-3 sentence plain-language summary",
              "keyFindings": ["finding 1", "finding 2", ...],
              "riskLevel": "LOW|MODERATE|HIGH|CRITICAL",
              "recommendations": ["recommendation 1", "recommendation 2", ...],
              "doctorQuestions": ["question to ask doctor 1", ...],
              "labValues": [
                {"name": "Hemoglobin", "value": "11.8", "unit": "g/dL", "referenceRange": "13.5-17.5", "status": "LOW"},
                ...
              ]
            }
            
            IMPORTANT: Provide health information only. Do not diagnose. Recommend professional consultation.
            """.formatted(userContext, extractedText);

        String response = ollamaClient.generate(prompt);

        MedicalReport.AnalysisResult result = parseAnalysisResult(response);
        result.setAiModel(defaultModel);
        result.setAnalyzedAt(LocalDateTime.now());

        return CompletableFuture.completedFuture(result);
    }

    // â”€â”€ Symptom Analysis â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public Map<String, String> analyzeSymptoms(String userId, SymptomRequest request) {
        String userContext = contextBuilder.buildUserContext(userId);
        String symptomsText = String.join(", ", request.getSymptoms());

        String prompt = """
            You are a medical AI assistant helping assess reported symptoms.
            
            PATIENT CONTEXT:
            %s
            
            Reported Symptoms: %s
            Duration: %s days
            Severity: %s
            
            Provide a response with:
            - category: general health area (e.g., "Respiratory", "Digestive", "Cardiovascular")
            - urgency: EMERGENCY|URGENT|SOON|ROUTINE
            - recommendation: clear action guidance
            
            IMPORTANT: This is health information only, NOT a diagnosis. Always recommend professional consultation.
            """.formatted(userContext, symptomsText,
                request.getAgeDays() != null ? request.getAgeDays() : "Unknown",
                request.getSeverity() != null ? request.getSeverity() : "Not specified");

        String response = ollamaClient.generate(prompt);
        response = safetyGuard.processResponse(response);

        return Map.of(
            "category", "General",
            "urgency", "ROUTINE",
            "recommendation", response
        );
    }

    // â”€â”€ Doctor Patient Summary â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public String generatePatientSummary(String patientUserId, String language) {
        String context = contextBuilder.buildUserContext(patientUserId);
        String prompt = "Generate a concise clinical summary for the following patient:\n\n" + context +
                        "\nProvide key health indicators, active conditions, and relevant history in a professional format.";
        return ollamaClient.generate(prompt);
    }

    // â”€â”€ Internal Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private AiChat getOrCreateSession(String userId, String sessionId) {
        if (sessionId != null) {
            return chatRepository.findByIdAndUserId(sessionId, userId)
                    .orElseGet(() -> createNewSession(userId));
        }
        return createNewSession(userId);
    }

    private AiChat createNewSession(String userId) {
        AiChat session = AiChat.builder()
                .userId(userId)
                .sessionTitle("Health Chat " + LocalDateTime.now().toLocalDate())
                .messages(new ArrayList<>())
                .active(true)
                .build();
        return chatRepository.save(session);
    }

    private void addMessageToSession(AiChat session, String role, String content, String model) {
        AiChat.ChatMessage msg = AiChat.ChatMessage.builder()
                .role(role)
                .content(content)
                .model(model)
                .timestamp(LocalDateTime.now())
                .flagged(false)
                .build();
        if (session.getMessages() == null) session.setMessages(new ArrayList<>());
        session.getMessages().add(msg);
        session.setMessageCount(session.getMessageCount() + 1);
        session.setLastMessageAt(LocalDateTime.now());
    }

    private String getUserLanguage(String userId) {
        return userRepository.findById(userId)
                .map(u -> u.getLanguagePreference() != null ? u.getLanguagePreference() : "en")
                .orElse("en");
    }

    private MedicalReport.AnalysisResult parseAnalysisResult(String response) {
        // Simple fallback parser â€” in production use Jackson to parse JSON from AI
        return MedicalReport.AnalysisResult.builder()
                .summary(response.length() > 500 ? response.substring(0, 500) : response)
                .keyFindings(List.of("See full analysis above"))
                .riskLevel("LOW")
                .recommendations(List.of("Please consult your healthcare provider for a complete evaluation."))
                .doctorQuestions(List.of("Please review these results with your doctor."))
                .build();
    }
}

