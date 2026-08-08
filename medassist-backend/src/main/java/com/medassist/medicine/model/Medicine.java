package com.medassist.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Medicine â€” tracks user medications with dosage, frequency and reminder times.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medicines")
@CompoundIndex(name = "user_active_idx", def = "{'user_id': 1, 'is_active': 1}")
public class Medicine {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("medicine_name")
    private String medicineName;

    @Field("generic_name")
    private String genericName;

    @Field("dosage")
    private String dosage;     // e.g., "500mg", "10ml"

    @Field("frequency")
    private String frequency;  // ONCE_DAILY, TWICE_DAILY, THREE_TIMES_DAILY, AS_NEEDED

    @Field("reminder_times")
    private List<LocalTime> reminderTimes;

    @Field("with_food")
    private Boolean withFood;

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    @Field("purpose")
    private String purpose;   // reason for taking

    @Field("prescribed_by")
    private String prescribedBy;

    @Field("notes")
    private String notes;

    @Field("is_active")
    @Builder.Default
    private boolean active = true;

    @Field("refill_reminder_days")
    @Builder.Default
    private Integer refillReminderDays = 7;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}

