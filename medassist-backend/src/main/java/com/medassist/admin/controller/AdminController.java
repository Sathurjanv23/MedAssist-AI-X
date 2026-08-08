package com.medassist.admin.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.audit.repository.AuditLogRepository;
import com.medassist.report.repository.MedicalReportRepository;
import com.medassist.user.repository.UserRepository;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin Panel", description = "System administration and analytics")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MedicalReportRepository reportRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/users")
    @Operation(summary = "Get all users (paginated, searchable)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> users = userService.getAllUsers(keyword,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Activate or deactivate a user account")
    public ResponseEntity<ApiResponse<Void>> setUserStatus(
            @PathVariable String userId,
            @RequestParam boolean active) {
        userService.setUserActive(userId, active);
        return ResponseEntity.ok(ApiResponse.success("User status updated"));
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Assign a role to a user")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @PathVariable String userId,
            @RequestParam String role) {
        userService.changeUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("Role assigned"));
    }

    @GetMapping("/analytics/overview")
    @Operation(summary = "Get system analytics overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        Map<String, Object> analytics = Map.of(
            "totalUsers", userRepository.count(),
            "activeUsers", userRepository.countByActive(true),
            "totalReports", reportRepository.count(),
            "reportsLast30Days", reportRepository.countByCreatedAtAfter(last30Days),
            "newUsersLast30Days", userRepository.countByCreatedAtAfter(last30Days)
        );
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }
}

