package com.medassist.ai.service;

import com.medassist.common.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * AI Safety Guard â€” prevents unsafe medical claims and adds disclaimers.
 *
 * <p>Responsibilities:
 * 1. Detect and block clearly harmful or irresponsible requests
 * 2. Flag responses that make definitive diagnoses
 * 3. Add appropriate medical disclaimers to AI responses
 */
@Slf4j
@Component
public class SafetyGuard {

    private static final List<String> BLOCKED_PATTERNS = List.of(
        "how to overdose",
        "suicide method",
        "kill myself",
        "self harm"
    );

    private static final List<Pattern> DIAGNOSIS_PATTERNS = List.of(
        Pattern.compile("you have (cancer|diabetes|hiv|aids)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("you are diagnosed with", Pattern.CASE_INSENSITIVE),
        Pattern.compile("your diagnosis is", Pattern.CASE_INSENSITIVE)
    );

    /**
     * Returns true if the user's message should be blocked.
     */
    public boolean isHarmfulRequest(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) return false;
        String lower = userMessage.toLowerCase();
        return BLOCKED_PATTERNS.stream().anyMatch(lower::contains);
    }

    /**
     * Post-processes AI response: adds disclaimer, softens diagnoses.
     */
    public String processResponse(String response) {
        if (response == null) return null;

        // Soften any definitive diagnosis statements
        String processed = response;
        for (Pattern p : DIAGNOSIS_PATTERNS) {
            processed = p.matcher(processed)
                    .replaceAll("based on these symptoms, it's possible that you may have");
        }

        // Append safety disclaimer
        if (!processed.contains("⚕️")) {
            processed += AppConstants.AI_SAFETY_DISCLAIMER;
        }

        return processed;
    }

    /**
     * Returns a safe response for blocked requests.
     */
    public String getBlockedResponse() {
        return "I'm not able to help with that request. " +
               "If you're experiencing a medical emergency, please call emergency services immediately.\n\n" +
               "For mental health support, please contact a crisis helpline in your area.\n\n" +
               AppConstants.AI_SAFETY_DISCLAIMER;
    }

    /**
     * Validates that the response contains appropriate uncertainty language.
     */
    public String ensureUncertaintyLanguage(String response) {
        if (response == null) return null;
        // Already has hedging language â€” return as-is
        List<String> hedges = Arrays.asList("may", "might", "could", "possible", "suggest", "consult");
        if (hedges.stream().anyMatch(response.toLowerCase()::contains)) {
            return response;
        }
        return response + "\n\n*Please consult a healthcare professional to confirm these findings.*";
    }
}

