package com.medassist.report.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Medical report response DTO for the frontend Reports page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponse {

    private String id;
    private String userId;
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String reportType;
    private String status;
    private String extractedText;
    private Map<String, Object> extractedEntities;
    private AnalysisResultResponse analysisResult;
    private LocalDateTime processingCompletedAt;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResultResponse {
        private String summary;
        private List<String> keyFindings;
        private String riskLevel;
        private List<String> recommendations;
        private List<String> doctorQuestions;
        private List<LabValueResponse> labValues;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabValueResponse {
        private String name;
        private String value;
        private String unit;
        private String referenceRange;
        private String status;
    }
}

