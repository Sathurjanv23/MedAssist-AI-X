package com.medassist.report.service;

import com.medassist.report.dto.response.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ReportService {
    ReportResponse uploadAndProcess(String userId, MultipartFile file, String reportType);
    ReportResponse getReportById(String userId, String reportId);
    Page<ReportResponse> getUserReports(String userId, Pageable pageable);
    void deleteReport(String userId, String reportId);
    CompletableFuture<ReportResponse> analyzeReportAsync(String reportId, String userId);
    ReportResponse getProcessingStatus(String userId, String reportId);
}

