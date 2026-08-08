package com.medassist.doctor.model;

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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consultation â€” appointment/consultation record between doctor and patient.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consultations")
@CompoundIndex(name = "doctor_patient_idx", def = "{'doctor_id': 1, 'patient_id': 1, 'scheduled_at': -1}")
public class Consultation {

    @Id
    private String id;

    @Indexed
    @Field("doctor_id")
    private String doctorId;

    @Indexed
    @Field("patient_id")
    private String patientId;

    @Field("consultation_type")
    private String consultationType; // ONLINE, IN_PERSON

    @Field("status")
    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED, NO_SHOW

    @Field("scheduled_at")
    private LocalDateTime scheduledAt;

    @Field("completed_at")
    private LocalDateTime completedAt;

    @Field("duration_minutes")
    private Integer durationMinutes;

    @Field("chief_complaint")
    private String chiefComplaint;

    @Field("doctor_notes")
    private String doctorNotes;

    @Field("diagnosis")
    private List<String> diagnosis;

    @Field("prescriptions")
    private List<Prescription> prescriptions;

    @Field("follow_up_date")
    private LocalDateTime followUpDate;

    @Field("follow_up_notes")
    private String followUpNotes;

    @Field("ai_patient_summary")
    private String aiPatientSummary;

    @Field("shared_report_ids")
    private List<String> sharedReportIds;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prescription {
        private String medicineName;
        private String dosage;
        private String frequency;
        private Integer durationDays;
        private String instructions;
    }
}

