package com.medassist.report.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.report.dto.response.ReportResponse;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.report.service.ReportService;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Medical Reports", description = "Upload and analyze medical reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a medical report for OCR + AI analysis")
    public ResponseEntity<ApiResponse<ReportResponse>> uploadReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "reportType", defaultValue = "GENERAL") String reportType) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        ReportResponse report = reportService.uploadAndProcess(user.getId(), file, reportType);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Report uploaded. AI analysis in progress.", report));
    }

    @GetMapping
    @Operation(summary = "Get all user reports (paginated)")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getReports(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Page<ReportResponse> reports = reportService.getUserReports(user.getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Get a specific report with analysis results")
    public ResponseEntity<ApiResponse<ReportResponse>> getReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String reportId) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        ReportResponse report = reportService.getReportById(user.getId(), reportId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/{reportId}/status")
    @Operation(summary = "Poll AI processing status for a report")
    public ResponseEntity<ApiResponse<ReportResponse>> getStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String reportId) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        ReportResponse report = reportService.getProcessingStatus(user.getId(), reportId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @DeleteMapping("/{reportId}")
    @Operation(summary = "Delete a report")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String reportId) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        reportService.deleteReport(user.getId(), reportId);
        return ResponseEntity.ok(ApiResponse.success("Report deleted"));
    }
}

