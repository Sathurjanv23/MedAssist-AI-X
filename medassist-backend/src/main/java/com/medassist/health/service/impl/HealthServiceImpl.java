package com.medassist.health.service.impl;

import com.medassist.health.dto.response.HealthTwinResponse;
import com.medassist.health.model.HealthData;
import com.medassist.common.exception.BadRequestException;
import com.medassist.user.model.MedicalProfile;
import com.medassist.health.repository.HealthDataRepository;
import com.medassist.user.repository.MedicalProfileRepository;
import com.medassist.report.repository.MedicalReportRepository;
import com.medassist.medicine.repository.MedicineRepository;
import com.medassist.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private final HealthDataRepository healthDataRepository;
    private final MedicalProfileRepository profileRepository;
    private final MedicalReportRepository reportRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public HealthData getHealthData(String userId) {
        return healthDataRepository.findByUserId(userId)
                .orElseGet(HealthData::new);
    }

    @Override
    public HealthData updateHealthData(String userId, HealthData data) {
        if (data == null || !hasHealthContent(data)) {
            throw new BadRequestException("At least one health metric must be provided");
        }

        HealthData existing = healthDataRepository.findByUserId(userId)
                .orElseGet(() -> HealthData.builder().userId(userId).build());
        existing.setSleepData(data.getSleepData());
        existing.setActivityData(data.getActivityData());
        existing.setNutritionData(data.getNutritionData());
        recalculateScore(existing);
        return healthDataRepository.save(existing);
    }

    @Override
    @Cacheable(value = "health_score", key = "#userId")
    public HealthTwinResponse getHealthTwin(String userId) {
        return healthDataRepository.findByUserId(userId)
                .map(this::mapToHealthTwinResponse)
                .orElseGet(() -> HealthTwinResponse.builder().build());
    }

    @Override
    @CacheEvict(value = "health_score", key = "#userId")
    public void recalculateHealthScore(String userId) {
        healthDataRepository.findByUserId(userId).ifPresent(hd -> {
            recalculateScore(hd);
            healthDataRepository.save(hd);
        });
    }

    private void recalculateScore(HealthData hd) {
        int score = 0;
        boolean hasAnyData = false;
        List<String> insights = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        // Sleep scoring
        if (hd.getSleepData() != null) {
            hasAnyData = true;
            double sleep = hd.getSleepData().getAvgHoursPerNight() != null
                    ? hd.getSleepData().getAvgHoursPerNight() : 0;
            if (sleep >= 7 && sleep <= 9) { score += 15; }
            else if (sleep >= 6) { score += 8; recommendations.add("Aim for 7-9 hours of sleep per night."); }
            else { recommendations.add("Your sleep is significantly below recommended levels. Prioritize sleep health."); }
        }

        // Activity scoring
        if (hd.getActivityData() != null) {
            hasAnyData = true;
            int steps = hd.getActivityData().getStepsPerDay() != null
                    ? hd.getActivityData().getStepsPerDay() : 0;
            if (steps >= 10000) { score += 20; insights.add("Excellent activity level!"); }
            else if (steps >= 7500) { score += 13; }
            else if (steps >= 5000) { score += 7; recommendations.add("Try to reach 10,000 steps per day."); }
            else { score -= 5; recommendations.add("Increase your daily activity â€” even short walks help!"); }
        }

        if (hd.getNutritionData() != null) {
            hasAnyData = true;
        }

        if (hasAnyData) {
            // Clamp score
            score = Math.max(0, Math.min(100, score));
            hd.setHealthScore(score);
            hd.setAiInsights(insights);
            hd.setRecommendations(recommendations);
            hd.setRiskLevel(score >= 75 ? "LOW" : score >= 50 ? "MODERATE" : "HIGH");
        } else {
            hd.setHealthScore(null);
            hd.setAiInsights(null);
            hd.setRecommendations(null);
            hd.setRiskLevel(null);
        }
    }

    private HealthTwinResponse mapToHealthTwinResponse(HealthData hd) {
        HealthTwinResponse.HealthTwinResponseBuilder builder = HealthTwinResponse.builder()
                .userId(hd.getUserId())
                .healthScore(hd.getHealthScore())
                .lifestyleScore(hd.getLifestyleScore())
                .riskScore(hd.getRiskScore())
                .riskLevel(hd.getRiskLevel())
                .aiInsights(hd.getAiInsights())
                .recommendations(hd.getRecommendations())
                .updatedAt(hd.getUpdatedAt());

        if (hd.getSleepData() != null) {
            builder.sleepData(HealthTwinResponse.SleepDataResponse.builder()
                    .avgHoursPerNight(hd.getSleepData().getAvgHoursPerNight())
                    .quality(hd.getSleepData().getQuality())
                    .bedtime(hd.getSleepData().getBedtime())
                    .wakeTime(hd.getSleepData().getWakeTime())
                    .build());
        }
        if (hd.getActivityData() != null) {
            builder.activityData(HealthTwinResponse.ActivityDataResponse.builder()
                    .stepsPerDay(hd.getActivityData().getStepsPerDay())
                    .activeMinutesPerDay(hd.getActivityData().getActiveMinutesPerDay())
                    .exerciseDaysPerWeek(hd.getActivityData().getExerciseDaysPerWeek())
                    .primaryExercise(hd.getActivityData().getPrimaryExercise())
                    .caloriesBurnedPerDay(hd.getActivityData().getCaloriesBurnedPerDay())
                    .build());
        }
        if (hd.getNutritionData() != null) {
            builder.nutritionData(HealthTwinResponse.NutritionDataResponse.builder()
                    .dailyCalories(hd.getNutritionData().getDailyCalories())
                    .proteinGrams(hd.getNutritionData().getProteinGrams())
                    .carbGrams(hd.getNutritionData().getCarbGrams())
                    .fatGrams(hd.getNutritionData().getFatGrams())
                    .waterLitres(hd.getNutritionData().getWaterLitres())
                    .build());
        }

        return builder.build();
    }

    private boolean hasHealthContent(HealthData data) {
        return data.getSleepData() != null || data.getActivityData() != null || data.getNutritionData() != null;
    }
}

