package com.medassist.user.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Medical profile create/update request DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalProfileRequest {

    @Positive(message = "Height must be positive")
    private Double heightCm;

    @Positive(message = "Weight must be positive")
    private Double weightKg;

    private BloodPressureRequest bloodPressure;

    private List<String> allergies;
    private List<String> chronicDiseases;
    private List<String> currentMedications;
    private List<String> pastSurgeries;
    private Map<String, String> familyHistory;

    private LifestyleRequest lifestyle;
    private EmergencyContactRequest emergencyContact;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodPressureRequest {
        private Integer systolic;
        private Integer diastolic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LifestyleRequest {
        private String smokingStatus;
        private String alcoholConsumption;
        private String exerciseFrequency;
        private String dietType;
        private Integer sleepHoursPerNight;
        private String stressLevel;
        private String occupation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContactRequest {
        private String name;
        private String relationship;
        private String phoneNumber;
        private String email;
    }
}

