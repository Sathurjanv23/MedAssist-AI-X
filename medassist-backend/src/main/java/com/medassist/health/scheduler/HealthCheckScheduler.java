package com.medassist.health.scheduler;

import com.medassist.user.repository.UserRepository;
import com.medassist.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Daily health score recalculation and summary generation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {

    private final UserRepository userRepository;
    private final HealthService healthService;

    /**
     * Recalculate health scores for all active users at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *") // midnight daily
    public void recalculateAllHealthScores() {
        log.info("Starting daily health score recalculation...");
        long startTime = System.currentTimeMillis();

        userRepository.findByRoleAndActive("ROLE_USER").forEach(user -> {
            try {
                healthService.recalculateHealthScore(user.getId());
            } catch (Exception e) {
                log.error("Health score recalculation failed for user {}: {}", user.getId(), e.getMessage());
            }
        });

        long duration = System.currentTimeMillis() - startTime;
        log.info("Health score recalculation completed in {}ms", duration);
    }
}

