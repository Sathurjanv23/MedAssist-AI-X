package com.medassist.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Symptom analysis request DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymptomRequest {

    @NotEmpty(message = "At least one symptom is required")
    private List<String> symptoms;

    private Integer ageDays;        // duration of symptoms in days
    private String severity;        // MILD, MODERATE, SEVERE
    private List<String> additionalContext;
    private String language;
}

