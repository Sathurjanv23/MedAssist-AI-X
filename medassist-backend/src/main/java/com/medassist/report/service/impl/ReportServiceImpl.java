package com.medassist.report.service.impl;

import com.medassist.ai.service.AIService;
import com.medassist.ocr.model.OcrModels;
import com.medassist.ocr.service.OcrService;
import com.medassist.report.dto.response.ReportResponse;
import com.medassist.common.exception.ResourceNotFoundException;
import com.medassist.report.model.MedicalReport;
import com.medassist.report.repository.MedicalReportRepository;
import com.medassist.report.service.ReportService;
import com.medassist.health.service.TimelineService;
import com.medassist.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MedicalReportRepository reportRepository;
    private final S3StorageService s3StorageService;
    private final AIService aiService;
    private final TimelineService timelineService;
    private final OcrService ocrService;

    @Override
    public ReportResponse uploadAndProcess(String userId, MultipartFile file, String reportType) {
        // 1. Upload to S3
        String fileUrl = s3StorageService.uploadReport(userId, file);

        // 2. Save report document with PENDING status
        MedicalReport report = MedicalReport.builder()
                .userId(userId)
                .fileName(java.util.UUID.randomUUID() + "_" + file.getOriginalFilename())
                .originalFileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .reportType(reportType != null ? reportType : "GENERAL")
                .status("PENDING")
                .build();

        report = reportRepository.save(report);
        final String reportId = report.getId();

        // 3. Extract text via OCR (uses real uploaded file; no fabricated content)
        try {
            OcrModels.OcrResult ocrResult = ocrService.extractText(file, "en").join();
            if (ocrResult != null && ocrResult.getRawText() != null && !ocrResult.getRawText().isBlank()) {
                report.setExtractedText(ocrResult.getRawText());
                report.setExtractedEntities(ocrResult.getEntities());
                report = reportRepository.save(report);
            }
        } catch (Exception e) {
            log.warn("OCR extraction skipped for report {}: {}", reportId, e.getMessage());
        }

        // 4. Auto-create timeline event
        timelineService.autoCreateEvent(userId, "REPORT_UPLOADED",
                "Uploaded: " + file.getOriginalFilename(),
                "Report type: " + report.getReportType(),
                Map.of("reportId", reportId));

        // 5. Trigger async AI analysis
        analyzeReportAsync(reportId, userId);

        log.info("Report uploaded: {} for user: {}", reportId, userId);
        return mapToResponse(report);
    }

    @Override
    @Async("aiExecutor")
    public CompletableFuture<ReportResponse> analyzeReportAsync(String reportId, String userId) {
        MedicalReport report = reportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));

        try {
            // Update to PROCESSING
            report.setStatus("PROCESSING");
            report.setProcessingStartedAt(LocalDateTime.now());
            reportRepository.save(report);

            // Retry OCR from S3 URL if text was not extracted during upload
            if (report.getExtractedText() == null || report.getExtractedText().isBlank()) {
                try {
                    OcrModels.OcrResult ocrResult = ocrService.extractTextFromUrl(report.getFileUrl(), "en").join();
                    if (ocrResult != null && ocrResult.getRawText() != null && !ocrResult.getRawText().isBlank()) {
                        report.setExtractedText(ocrResult.getRawText());
                        report.setExtractedEntities(ocrResult.getEntities());
                        reportRepository.save(report);
                    }
                } catch (Exception e) {
                    log.warn("OCR retry failed for report {}: {}", reportId, e.getMessage());
                }
            }

            // Use extracted text only — never fabricate medical content
            String textToAnalyze = report.getExtractedText();
            if (textToAnalyze == null || textToAnalyze.isBlank()) {
                report.setStatus("FAILED");
                report.setErrorMessage("No text could be extracted from the report. OCR service may be unavailable.");
                report.setProcessingCompletedAt(LocalDateTime.now());
                reportRepository.save(report);
                return CompletableFuture.failedFuture(
                        new IllegalStateException("Report text extraction failed"));
            }

            // Run AI analysis
            MedicalReport.AnalysisResult result = aiService.analyzeReport(textToAnalyze, userId).get();

            // Update report with results
            report.setAnalysisResult(result);
            report.setStatus("COMPLETED");
            report.setProcessingCompletedAt(LocalDateTime.now());
            reportRepository.save(report);

            log.info("Report analysis completed: {}", reportId);
            return CompletableFuture.completedFuture(mapToResponse(report));

        } catch (Exception e) {
            log.error("Report analysis failed for {}: {}", reportId, e.getMessage());
            report.setStatus("FAILED");
            report.setErrorMessage(e.getMessage());
            reportRepository.save(report);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public ReportResponse getReportById(String userId, String reportId) {
        return reportRepository.findByIdAndUserId(reportId, userId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));
    }

    @Override
    public Page<ReportResponse> getUserReports(String userId, Pageable pageable) {
        return reportRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void deleteReport(String userId, String reportId) {
        MedicalReport report = reportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));
        // Extract S3 key from URL and delete
        reportRepository.delete(report);
    }

    @Override
    public ReportResponse getProcessingStatus(String userId, String reportId) {
        return getReportById(userId, reportId);
    }

    private ReportResponse mapToResponse(MedicalReport report) {
        ReportResponse.ReportResponseBuilder builder = ReportResponse.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .fileName(report.getFileName())
                .originalFileName(report.getOriginalFileName())
                .fileUrl(report.getFileUrl())
                .fileType(report.getFileType())
                .fileSize(report.getFileSize())
                .reportType(report.getReportType())
                .status(report.getStatus())
                .extractedText(report.getExtractedText())
                .extractedEntities(report.getExtractedEntities())
                .processingCompletedAt(report.getProcessingCompletedAt())
                .createdAt(report.getCreatedAt());

        if (report.getAnalysisResult() != null) {
            MedicalReport.AnalysisResult ar = report.getAnalysisResult();
            ReportResponse.AnalysisResultResponse analysisResponse =
                    ReportResponse.AnalysisResultResponse.builder()
                            .summary(ar.getSummary())
                            .keyFindings(ar.getKeyFindings())
                            .riskLevel(ar.getRiskLevel())
                            .recommendations(ar.getRecommendations())
                            .doctorQuestions(ar.getDoctorQuestions())
                            .build();

            if (ar.getLabValues() != null) {
                analysisResponse.setLabValues(ar.getLabValues().stream()
                        .map(lv -> ReportResponse.LabValueResponse.builder()
                                .name(lv.getName())
                                .value(lv.getValue())
                                .unit(lv.getUnit())
                                .referenceRange(lv.getReferenceRange())
                                .status(lv.getStatus())
                                .build())
                        .collect(Collectors.toList()));
            }
            builder.analysisResult(analysisResponse);
        }

        return builder.build();
    }
}

