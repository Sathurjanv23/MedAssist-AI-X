package com.medassist.health.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.health.model.HealthTimeline;
import com.medassist.health.service.TimelineService;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Health Timeline", description = "Health event timeline management")
public class TimelineController {

    private final TimelineService timelineService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user health timeline (paginated)")
    public ResponseEntity<ApiResponse<Page<HealthTimeline>>> getTimeline(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Page<HealthTimeline> timeline = timelineService.getUserTimeline(user.getId(),
                PageRequest.of(page, size, Sort.by("eventDate").descending()));
        return ResponseEntity.ok(ApiResponse.success(timeline));
    }

    @PostMapping
    @Operation(summary = "Add a custom health timeline event")
    public ResponseEntity<ApiResponse<HealthTimeline>> addEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody HealthTimeline event) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        HealthTimeline saved = timelineService.addEvent(user.getId(), event);
        return ResponseEntity.ok(ApiResponse.success("Timeline event added", saved));
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Delete a timeline event")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String eventId) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        timelineService.deleteEvent(user.getId(), eventId);
        return ResponseEntity.ok(ApiResponse.success("Event deleted"));
    }
}

