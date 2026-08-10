package com.medassist.ai.controller;

import com.medassist.ai.service.AIService;
import com.medassist.ai.service.OllamaClient;
import com.medassist.common.response.ApiResponse;
import com.medassist.ai.dto.request.ChatRequest;
import com.medassist.ai.dto.request.SymptomRequest;
import com.medassist.ai.dto.response.ChatResponse;
import com.medassist.ai.model.AiChat;
import com.medassist.ai.model.OllamaModels;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "AI Engine", description = "AI chat assistant, report analysis and symptom checking")
public class AIController {

    private final AIService aiService;
    private final UserService userService;
    private final OllamaClient ollamaClient;

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI health assistant")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChatRequest request) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        ChatResponse response = aiService.chat(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/analyze-report")
    @Operation(summary = "Trigger AI analysis of a specific report by text")
    public ResponseEntity<ApiResponse<String>> analyzeReportText(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("BAD_REQUEST", "Text is required"));
        }
        // Trigger async analysis and return immediately
        aiService.analyzeReport(text, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Analysis triggered. Check back for results."));
    }

    @PostMapping("/symptoms")
    @Operation(summary = "AI symptom assessment (NOT a medical diagnosis)")
    public ResponseEntity<ApiResponse<Map<String, String>>> analyzeSymptoms(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SymptomRequest request) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Map<String, String> result = aiService.analyzeSymptoms(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/status")
    @Operation(summary = "Check if AI service (Ollama) is running")
    public ResponseEntity<ApiResponse<Map<String, Object>>> aiStatus() {
        boolean available = ollamaClient.isAvailable();
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "status", available ? "UP" : "DOWN",
            "available", available,
            "message", available
                    ? "AI service is reachable"
                    : "AI service is not reachable — verify Ollama is running"
        )));
    }
}

