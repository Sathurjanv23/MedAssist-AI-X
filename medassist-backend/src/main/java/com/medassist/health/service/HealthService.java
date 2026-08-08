package com.medassist.health.service;

import com.medassist.health.dto.response.HealthTwinResponse;
import com.medassist.health.model.HealthData;

public interface HealthService {
    HealthData getHealthData(String userId);
    HealthData updateHealthData(String userId, HealthData healthData);
    HealthTwinResponse getHealthTwin(String userId);
    void recalculateHealthScore(String userId);
}

