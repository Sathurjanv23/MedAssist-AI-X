package com.medassist.health.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Health Twin response DTO â€” used by the Health Twin dashboard page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthTwinResponse {

    private String userId;
    private Integer healthScore;
    private Integer lifestyleScore;
    private Integer riskScore;
    private String riskLevel;

    private SleepDataResponse sleepData;
    private ActivityDataResponse activityData;
    private NutritionDataResponse nutritionData;

    private List<String> aiInsights;
    private List<String> recommendations;
    private List<HealthTrendResponse> trends;

    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SleepDataResponse {
        private Double avgHoursPerNight;
        private String quality;
        private Integer bedtime;
        private Integer wakeTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDataResponse {
        private Integer stepsPerDay;
        private Integer activeMinutesPerDay;
        private Integer exerciseDaysPerWeek;
        private String primaryExercise;
        private Double caloriesBurnedPerDay;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionDataResponse {
        private Integer dailyCalories;
        private Double proteinGrams;
        private Double carbGrams;
        private Double fatGrams;
        private Double waterLitres;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthTrendResponse {
        private String metric;
        private String direction;
        private Double previousValue;
        private Double currentValue;
        private LocalDateTime recordedAt;
    }
}

