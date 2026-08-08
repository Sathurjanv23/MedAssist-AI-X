package com.medassist.doctor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Doctor â€” professional profile linked to a User account.
 * Doctors have ROLE_DOCTOR and access patient data with explicit permissions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("user_id")
    private String userId;

    @Indexed(unique = true, sparse = true)
    @Field("license_number")
    private String licenseNumber;

    @Field("specialization")
    private String specialization;

    @Field("sub_specializations")
    private List<String> subSpecializations;

    @Field("hospital")
    private String hospital;

    @Field("clinic_address")
    private String clinicAddress;

    @Field("experience_years")
    private Integer experienceYears;

    @Field("qualifications")
    private List<String> qualifications;

    @Field("consultation_fee")
    private Double consultationFee;

    @Field("availability")
    private DoctorAvailability availability;

    @Field("verified_status")
    @Builder.Default
    private String verifiedStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Field("verified_at")
    private LocalDateTime verifiedAt;

    @Field("verified_by")
    private String verifiedBy;

    @Field("rating")
    private Double rating;

    @Field("total_consultations")
    @Builder.Default
    private Integer totalConsultations = 0;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorAvailability {
        private List<String> days;          // MON, TUE, WED, THU, FRI, SAT, SUN
        private String startTime;           // HH:mm
        private String endTime;             // HH:mm
        private Boolean onlineConsultation;
        private Boolean inPersonConsultation;
    }
}

