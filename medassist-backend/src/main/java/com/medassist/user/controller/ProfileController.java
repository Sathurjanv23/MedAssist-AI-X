package com.medassist.user.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.user.dto.request.MedicalProfileRequest;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.user.model.MedicalProfile;
import com.medassist.user.service.ProfileService;
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
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Medical Profile", description = "User medical profile management")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get medical profile")
    public ResponseEntity<ApiResponse<MedicalProfile>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        MedicalProfile profile = profileService.getProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping
    @Operation(summary = "Create or update medical profile")
    public ResponseEntity<ApiResponse<MedicalProfile>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MedicalProfileRequest request) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        MedicalProfile profile = profileService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Medical profile updated", profile));
    }
}

