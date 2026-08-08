package com.medassist.medicine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Medicine create/update request DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineRequest {

    @NotBlank(message = "Medicine name is required")
    @Size(max = 200)
    private String medicineName;

    private String genericName;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Pattern(regexp = "^(ONCE_DAILY|TWICE_DAILY|THREE_TIMES_DAILY|FOUR_TIMES_DAILY|AS_NEEDED|WEEKLY|MONTHLY)$",
             message = "Invalid frequency value")
    private String frequency;

    private List<String> reminderTimes; // HH:mm format strings

    private Boolean withFood;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private String purpose;
    private String prescribedBy;

    @Size(max = 500)
    private String notes;

    private Integer refillReminderDays;
}

