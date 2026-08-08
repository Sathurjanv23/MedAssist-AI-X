package com.medassist.medicine.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.medicine.dto.request.MedicineRequest;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.medicine.model.Medicine;
import com.medassist.medicine.service.MedicineService;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Medicines", description = "Medicine tracking and reminder management")
public class MedicineController {

    private final MedicineService medicineService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all medicines for current user")
    public ResponseEntity<ApiResponse<List<Medicine>>> getMedicines(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        List<Medicine> medicines = medicineService.getUserMedicines(user.getId(), activeOnly);
        return ResponseEntity.ok(ApiResponse.success(medicines));
    }

    @PostMapping
    @Operation(summary = "Add a new medicine")
    public ResponseEntity<ApiResponse<Medicine>> addMedicine(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MedicineRequest request) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Medicine medicine = medicineService.addMedicine(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medicine added", medicine));
    }

    @PutMapping("/{medicineId}")
    @Operation(summary = "Update a medicine")
    public ResponseEntity<ApiResponse<Medicine>> updateMedicine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String medicineId,
            @Valid @RequestBody MedicineRequest request) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Medicine medicine = medicineService.updateMedicine(user.getId(), medicineId, request);
        return ResponseEntity.ok(ApiResponse.success("Medicine updated", medicine));
    }

    @DeleteMapping("/{medicineId}")
    @Operation(summary = "Delete a medicine")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String medicineId) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        medicineService.deleteMedicine(user.getId(), medicineId);
        return ResponseEntity.ok(ApiResponse.success("Medicine deleted"));
    }

    @PatchMapping("/{medicineId}/toggle")
    @Operation(summary = "Activate or deactivate a medicine")
    public ResponseEntity<ApiResponse<Void>> toggleMedicine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String medicineId,
            @RequestParam boolean active) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        medicineService.toggleMedicineActive(user.getId(), medicineId, active);
        return ResponseEntity.ok(ApiResponse.success("Medicine " + (active ? "activated" : "deactivated")));
    }
}

