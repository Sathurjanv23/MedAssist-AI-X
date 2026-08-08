package com.medassist.health.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.health.dto.response.HealthTwinResponse;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.health.model.HealthData;
import com.medassist.health.service.HealthService;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Health & Health Twin", description = "Health data and AI Health Twin management")
public class HealthController {

    private final HealthService healthService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user health data")
    public ResponseEntity<ApiResponse<HealthData>> getHealthData(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        HealthData hd = healthService.getHealthData(user.getId());
        return ResponseEntity.ok(ApiResponse.success(hd));
    }

    @GetMapping("/twin")
    @Operation(summary = "Get AI Health Twin â€” computed health score and insights")
    public ResponseEntity<ApiResponse<HealthTwinResponse>> getHealthTwin(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        HealthTwinResponse twin = healthService.getHealthTwin(user.getId());
        return ResponseEntity.ok(ApiResponse.success(twin));
    }

    @PutMapping
    @Operation(summary = "Update health data (sleep, activity, nutrition)")
    public ResponseEntity<ApiResponse<HealthData>> updateHealthData(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody HealthData healthData) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        HealthData updated = healthService.updateHealthData(user.getId(), healthData);
        return ResponseEntity.ok(ApiResponse.success("Health data updated", updated));
    }

    @PostMapping("/recalculate")
    @Operation(summary = "Trigger health score recalculation")
    public ResponseEntity<ApiResponse<Void>> recalculate(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        healthService.recalculateHealthScore(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Health score recalculated"));
    }
}

