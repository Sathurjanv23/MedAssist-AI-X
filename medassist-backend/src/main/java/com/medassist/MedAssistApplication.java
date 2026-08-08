package com.medassist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MedAssist AI X â€” Main Application Entry Point
 *
 * <p>Your Personal AI Healthcare Operating System
 *
 * <p>Architecture:
 * <pre>
 *   MedAssistApplication
 *       â”œâ”€â”€ @EnableCaching       â€” Redis-backed caching
 *       â”œâ”€â”€ @EnableAsync         â€” CompletableFuture async tasks (OCR, AI)
 *       â”œâ”€â”€ @EnableScheduling    â€” Cron-based medicine reminders, health checks
 *       â””â”€â”€ @EnableMongoAuditing â€” Auto-populated createdAt/updatedAt
 * </pre>
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableMongoAuditing
public class MedAssistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedAssistApplication.class, args);
    }
}

