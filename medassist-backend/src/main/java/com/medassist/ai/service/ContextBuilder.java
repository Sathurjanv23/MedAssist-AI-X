package com.medassist.ai.service;

import com.medassist.ai.model.AiChat;
import com.medassist.ai.model.OllamaModels;
import com.medassist.health.model.HealthData;
import com.medassist.user.model.MedicalProfile;
import com.medassist.user.model.User;
import com.medassist.health.repository.HealthDataRepository;
import com.medassist.user.repository.MedicalProfileRepository;
import com.medassist.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Context Builder â€” assembles user health context for AI prompts.
 * Retrieves and formats user health data into structured context for the LLM.
 */
@Component
@RequiredArgsConstructor
public class ContextBuilder {

    private final UserRepository userRepository;
    private final MedicalProfileRepository profileRepository;
    private final HealthDataRepository healthDataRepository;

    /**
     * Build a comprehensive health context string for the AI.
     */
    public String buildUserContext(String userId) {
        StringBuilder ctx = new StringBuilder();

        // User basic info
        userRepository.findById(userId).ifPresent(user -> {
            ctx.append("PATIENT INFORMATION:\n");
            ctx.append("Name: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
            if (user.getDateOfBirth() != null) {
                ctx.append("DOB: ").append(user.getDateOfBirth()).append("\n");
            }
            if (user.getGender() != null) ctx.append("Gender: ").append(user.getGender()).append("\n");
            if (user.getBloodGroup() != null) ctx.append("Blood Group: ").append(user.getBloodGroup()).append("\n");
            ctx.append("\n");
        });

        // Medical profile
        profileRepository.findByUserId(userId)
            .filter(this::hasProfileData)
            .ifPresent(profile -> {
            ctx.append("MEDICAL PROFILE:\n");
            if (profile.getHeightCm() != null) ctx.append("Height: ").append(profile.getHeightCm()).append("cm\n");
            if (profile.getWeightKg() != null) ctx.append("Weight: ").append(profile.getWeightKg()).append("kg\n");
            if (profile.getBmi() != null) ctx.append("BMI: ").append(profile.getBmi()).append("\n");
            if (profile.getBloodPressure() != null) {
                ctx.append("Blood Pressure: ")
                   .append(profile.getBloodPressure().getSystolic()).append("/")
                   .append(profile.getBloodPressure().getDiastolic())
                   .append(" (").append(profile.getBloodPressure().getCategory()).append(")\n");
            }
            if (profile.getAllergies() != null && !profile.getAllergies().isEmpty()) {
                ctx.append("Allergies: ").append(String.join(", ", profile.getAllergies())).append("\n");
            }
            if (profile.getChronicDiseases() != null && !profile.getChronicDiseases().isEmpty()) {
                ctx.append("Chronic Conditions: ").append(String.join(", ", profile.getChronicDiseases())).append("\n");
            }
            if (profile.getCurrentMedications() != null && !profile.getCurrentMedications().isEmpty()) {
                ctx.append("Current Medications: ").append(String.join(", ", profile.getCurrentMedications())).append("\n");
            }
            ctx.append("\n");
        });

        // Health data
        healthDataRepository.findByUserId(userId)
                .filter(this::hasHealthData)
                .ifPresent(hd -> {
            ctx.append("HEALTH METRICS:\n");
            if (hd.getHealthScore() != null) {
                ctx.append("Health Score: ").append(hd.getHealthScore()).append("/100\n");
            }
            if (hd.getRiskLevel() != null) {
                ctx.append("Risk Level: ").append(hd.getRiskLevel()).append("\n");
            }
            if (hd.getSleepData() != null) {
                ctx.append("Sleep: ")
                        .append(hd.getSleepData().getAvgHoursPerNight() != null ? hd.getSleepData().getAvgHoursPerNight() : "unknown")
                        .append(" hours/night\n");
            }
            if (hd.getActivityData() != null) {
                ctx.append("Activity: ")
                        .append(hd.getActivityData().getStepsPerDay() != null ? hd.getActivityData().getStepsPerDay() : "unknown")
                        .append(" steps/day\n");
            }
            if (hd.getNutritionData() != null) {
                ctx.append("Nutrition: ")
                        .append(hd.getNutritionData().getDailyCalories() != null ? hd.getNutritionData().getDailyCalories() : "unknown")
                        .append(" kcal/day\n");
            }
            ctx.append("\n");
        });

        return ctx.toString();
    }

    /**
     * Build chat history as Ollama message list.
     */
    public List<OllamaModels.OllamaMessage> buildChatHistory(AiChat session, int maxMessages) {
        List<AiChat.ChatMessage> history = session.getMessages();
        if (history == null) return new ArrayList<>();

        int start = Math.max(0, history.size() - maxMessages);
        return history.subList(start, history.size()).stream()
                .map(m -> OllamaModels.OllamaMessage.builder()
                        .role(m.getRole())
                        .content(m.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Build the system prompt for the AI health assistant.
     */
    public String buildSystemPrompt(String userContext, String language) {
        String langInstruction = switch (language != null ? language : "en") {
            case "ta" -> "IMPORTANT: Respond in Tamil language.";
            case "si" -> "IMPORTANT: Respond in Sinhala language.";
            default -> "Respond in English.";
        };

        return """
            You are MedAssist AI, a compassionate and knowledgeable AI healthcare assistant.
            
            Your role is to:
            - Help users understand their medical reports and health data
            - Provide evidence-based health information and education
            - Suggest lifestyle improvements and preventive care
            - Explain medical terms in simple, friendly language
            - Encourage professional medical consultation for diagnosis and treatment
            
            CRITICAL RULES:
            - NEVER provide definitive medical diagnoses
            - ALWAYS recommend consulting a qualified healthcare professional
            - Add appropriate medical disclaimers to responses
            - Do not prescribe medications or specific dosages
            - Be empathetic, encouraging, and non-alarming
            
            """ + langInstruction + """
            
            CURRENT USER CONTEXT:
            """ + userContext;
    }

    private boolean hasProfileData(MedicalProfile profile) {
        return profile.getHeightCm() != null
                || profile.getWeightKg() != null
                || profile.getBloodPressure() != null
                || profile.getBmi() != null
                || (profile.getAllergies() != null && !profile.getAllergies().isEmpty())
                || (profile.getChronicDiseases() != null && !profile.getChronicDiseases().isEmpty())
                || (profile.getCurrentMedications() != null && !profile.getCurrentMedications().isEmpty())
                || (profile.getPastSurgeries() != null && !profile.getPastSurgeries().isEmpty())
                || (profile.getFamilyHistory() != null && !profile.getFamilyHistory().isEmpty())
                || profile.getLifestyle() != null
                || profile.getEmergencyContact() != null;
    }

    private boolean hasHealthData(HealthData hd) {
        return hd.getHealthScore() != null
                || hd.getRiskLevel() != null
                || hd.getSleepData() != null
                || hd.getActivityData() != null
                || hd.getNutritionData() != null
                || (hd.getAiInsights() != null && !hd.getAiInsights().isEmpty())
                || (hd.getRecommendations() != null && !hd.getRecommendations().isEmpty());
    }
}

