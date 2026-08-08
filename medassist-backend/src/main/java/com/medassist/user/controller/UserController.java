package com.medassist.user.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.user.dto.request.ProfileUpdateRequest;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * User profile management controller.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request) {
        // Get userId from DB via email
        UserResponse current = userService.getCurrentUser(userDetails.getUsername());
        UserResponse updated = userService.updateProfile(current.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping("/me/profile-image")
    @Operation(summary = "Upload profile image")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        UserResponse current = userService.getCurrentUser(userDetails.getUsername());
        String imageUrl = userService.uploadProfileImage(current.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Profile image uploaded", imageUrl));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete/deactivate account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse current = userService.getCurrentUser(userDetails.getUsername());
        userService.deleteAccount(current.getId());
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully"));
    }
}

