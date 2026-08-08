package com.medassist.health.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * HealthTimeline â€” chronological record of significant health events.
 * Powers the Health Timeline UI showing past â†’ present â†’ future health journey.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "health_timeline")
@CompoundIndex(name = "user_date_idx", def = "{'user_id': 1, 'event_date': -1}")
public class HealthTimeline {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("event_type")
    private String eventType;
    // REPORT_UPLOADED, MEDICINE_STARTED, MEDICINE_STOPPED, DOCTOR_VISIT,
    // HEALTH_SCORE_CHANGED, SYMPTOM_LOGGED, DIAGNOSIS, SURGERY, VACCINATION, CUSTOM

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("metadata")
    private Map<String, Object> metadata; // flexible key-value for event-specific data

    @Field("icon")
    private String icon; // lucide icon name for frontend rendering

    @Field("color")
    private String color; // Tailwind color class for frontend

    @Field("event_date")
    private LocalDate eventDate;

    @Field("linked_report_id")
    private String linkedReportId;

    @Field("linked_medicine_id")
    private String linkedMedicineId;

    @Field("is_milestone")
    @Builder.Default
    private boolean milestone = false;

    @Field("created_at")
    private LocalDateTime createdAt;
}

