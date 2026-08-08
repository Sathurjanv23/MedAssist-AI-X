package com.medassist.doctor.controller;

import com.medassist.common.response.ApiResponse;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.doctor.model.Consultation;
import com.medassist.doctor.model.Doctor;
import com.medassist.doctor.repository.ConsultationRepository;
import com.medassist.doctor.repository.DoctorRepository;
import com.medassist.user.repository.UserRepository;
import com.medassist.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Doctor Portal", description = "Doctor profile and patient management")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final ConsultationRepository consultationRepository;
    private final UserService userService;

    @PostMapping("/profile")
    @Operation(summary = "Create or update doctor profile")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Doctor>> upsertDoctorProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Doctor doctorProfile) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        doctorProfile.setUserId(user.getId());

        Doctor existing = doctorRepository.findByUserId(user.getId()).orElse(null);
        if (existing != null) {
            doctorProfile.setId(existing.getId());
        }

        Doctor saved = doctorRepository.save(doctorProfile);
        return ResponseEntity.status(existing == null ? HttpStatus.CREATED : HttpStatus.OK)
                .body(ApiResponse.success("Doctor profile saved", saved));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get doctor profile")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.success(doctor));
    }

    @GetMapping("/consultations")
    @Operation(summary = "Get doctor's consultations")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<Consultation>>> getConsultations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        Page<Consultation> consultations = consultationRepository
                .findByDoctorIdOrderByScheduledAtDesc(doctor.getId(),
                        PageRequest.of(page, size, Sort.by("scheduledAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(consultations));
    }

    @PostMapping("/consultation")
    @Operation(summary = "Create a new consultation record")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Consultation>> createConsultation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Consultation consultation) {
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        consultation.setDoctorId(doctor.getId());
        Consultation saved = consultationRepository.save(consultation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Consultation created", saved));
    }
}

