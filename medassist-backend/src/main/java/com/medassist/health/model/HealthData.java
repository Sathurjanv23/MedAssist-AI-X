package com.medassist.health.model;

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
 * HealthData â€” aggregated health metrics computed by the AI Health Twin engine.
 * Stores sleep, activity, nutrition tracking plus overall health scoring.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "health_data")
public class HealthData {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    // â”€â”€ Health Scoring â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("health_score")
    private Integer healthScore;

    @Field("lifestyle_score")
    private Integer lifestyleScore;

    @Field("risk_score")
    private Integer riskScore;

    @Field("risk_level")
    private String riskLevel; // LOW, MODERATE, HIGH, CRITICAL

    // â”€â”€ Sleep â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("sleep_data")
    private SleepData sleepData;

    // â”€â”€ Activity â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("activity_data")
    private ActivityData activityData;

    // â”€â”€ Nutrition â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("nutrition_data")
    private NutritionData nutritionData;

    // â”€â”€ Trends â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("health_trends")
    private List<HealthTrend> healthTrends;

    // â”€â”€ AI Insights â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("ai_insights")
    private List<String> aiInsights;

    @Field("recommendations")
    private List<String> recommendations;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    // â”€â”€ Embedded â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SleepData {
        private Double avgHoursPerNight;
        private String quality; // poor, fair, good, excellent
        private Integer bedtime;   // hour of day 0-23
        private Integer wakeTime;  // hour of day 0-23
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityData {
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
    public static class NutritionData {
        private Integer dailyCalories;
        private Double proteinGrams;
        private Double carbGrams;
        private Double fatGrams;
        private Double waterLitres;
        private List<String> avoidFoods;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthTrend {
        private String metric;       // e.g. "hemoglobin", "blood_pressure"
        private String direction;    // UP, DOWN, STABLE
        private Double previousValue;
        private Double currentValue;
        private LocalDateTime recordedAt;
    }
}

