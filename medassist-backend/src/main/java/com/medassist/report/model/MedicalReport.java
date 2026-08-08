package com.medassist.report.model;

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
import java.util.Map;

/**
 * MedicalReport â€” stores uploaded medical documents plus OCR and AI analysis results.
 *
 * <p>Status lifecycle: PENDING â†’ PROCESSING â†’ COMPLETED | FAILED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_reports")
@CompoundIndex(name = "user_created_idx", def = "{'user_id': 1, 'created_at': -1}")
public class MedicalReport {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("file_name")
    private String fileName;

    @Field("original_file_name")
    private String originalFileName;

    @Field("file_url")
    private String fileUrl;

    @Field("file_size")
    private Long fileSize;

    @Field("file_type")
    private String fileType;

    @Field("report_type")
    private String reportType; // BLOOD_TEST, XRAY, MRI, CT_SCAN, URINE_TEST, GENERAL

    // â”€â”€ OCR Results â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("extracted_text")
    private String extractedText;

    @Field("extracted_entities")
    private Map<String, Object> extractedEntities;

    // â”€â”€ AI Analysis â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("analysis_result")
    private AnalysisResult analysisResult;

    // â”€â”€ Processing State â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Field("status")
    @Builder.Default
    private String status = "PENDING"; // PENDING, PROCESSING, COMPLETED, FAILED

    @Field("error_message")
    private String errorMessage;

    @Field("processing_started_at")
    private LocalDateTime processingStartedAt;

    @Field("processing_completed_at")
    private LocalDateTime processingCompletedAt;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    // â”€â”€ AI Analysis Embedded â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResult {
        private String summary;
        private List<String> keyFindings;
        private String riskLevel;    // LOW, MODERATE, HIGH, CRITICAL
        private List<String> recommendations;
        private List<String> doctorQuestions;
        private List<LabValue> labValues;
        private String aiModel;
        private LocalDateTime analyzedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabValue {
        private String name;
        private String value;
        private String unit;
        private String referenceRange;
        private String status; // NORMAL, LOW, HIGH, CRITICAL
    }
}

