package com.medassist.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Medical profile â€” one-to-one with User, stores comprehensive health background.
 * Stored as a separate document (not embedded) because:
 * 1. It's large and evolves independently
 * 2. It's read/written on its own â€” not always needed with user auth
 * 3. Allows efficient partial updates without loading full user document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_profiles")
public class MedicalProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("user_id")
    private String userId;

    // â”€â”€ Biometrics â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("height_cm")
    private Double heightCm;

    @Field("weight_kg")
    private Double weightKg;

    @Field("blood_pressure")
    private BloodPressure bloodPressure;

    @Field("bmi")
    private Double bmi;

    // â”€â”€ Medical History â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("allergies")
    private List<String> allergies;

    @Field("chronic_diseases")
    private List<String> chronicDiseases;

    @Field("current_medications")
    private List<String> currentMedications;

    @Field("past_surgeries")
    private List<String> pastSurgeries;

    @Field("family_history")
    private Map<String, String> familyHistory;

    // â”€â”€ Lifestyle â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("lifestyle")
    private Lifestyle lifestyle;

    // â”€â”€ Emergency â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("emergency_contact")
    private EmergencyContact emergencyContact;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    // â”€â”€ Embedded Documents â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodPressure {
        private Integer systolic;
        private Integer diastolic;
        private String category; // Normal, Elevated, High Stage 1, High Stage 2
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lifestyle {
        private String smokingStatus;     // never, former, current
        private String alcoholConsumption; // none, occasional, moderate, heavy
        private String exerciseFrequency; // none, 1-2/week, 3-4/week, 5+/week
        private String dietType;          // vegetarian, vegan, omnivore, etc.
        private Integer sleepHoursPerNight;
        private String stressLevel;       // low, moderate, high
        private String occupation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContact {
        private String name;
        private String relationship;
        private String phoneNumber;
        private String email;
    }
}

