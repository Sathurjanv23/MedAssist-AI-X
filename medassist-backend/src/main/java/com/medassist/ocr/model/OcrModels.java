package com.medassist.ocr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Shared models for OCR processing pipeline.
 */
public class OcrModels {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OcrRequest {
        private String imageBase64;
        private String imageUrl;
        private String language;    // eng, tam, sin
        private String documentType; // BLOOD_TEST, PRESCRIPTION, SCAN, GENERAL
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OcrResult {
        private String rawText;
        private Double confidence;           // 0.0 - 1.0
        private Map<String, Object> entities; // extracted: medicine_names, lab_values, dates
        private List<String> warnings;
        private boolean success;
        private String error;
    }
}

