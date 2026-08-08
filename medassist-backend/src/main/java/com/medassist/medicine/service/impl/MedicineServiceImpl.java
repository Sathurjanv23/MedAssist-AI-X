package com.medassist.medicine.service.impl;

import com.medassist.medicine.dto.request.MedicineRequest;
import com.medassist.common.exception.ResourceNotFoundException;
import com.medassist.medicine.model.Medicine;
import com.medassist.medicine.repository.MedicineRepository;
import com.medassist.medicine.service.MedicineService;
import com.medassist.health.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final TimelineService timelineService;

    @Override
    public Medicine addMedicine(String userId, MedicineRequest request) {
        List<LocalTime> reminderTimes = request.getReminderTimes() != null
                ? request.getReminderTimes().stream()
                    .map(LocalTime::parse)
                    .collect(Collectors.toList())
                : List.of();

        Medicine medicine = Medicine.builder()
                .userId(userId)
                .medicineName(request.getMedicineName())
                .genericName(request.getGenericName())
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .reminderTimes(reminderTimes)
                .withFood(request.getWithFood())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .purpose(request.getPurpose())
                .prescribedBy(request.getPrescribedBy())
                .notes(request.getNotes())
                .refillReminderDays(request.getRefillReminderDays() != null ? request.getRefillReminderDays() : 7)
                .active(true)
                .build();

        medicine = medicineRepository.save(medicine);

        // Auto-create timeline event
        timelineService.autoCreateEvent(userId, "MEDICINE_STARTED",
                "Started " + medicine.getMedicineName(),
                medicine.getDosage() + " - " + medicine.getFrequency(),
                Map.of("medicineId", medicine.getId(), "medicineName", medicine.getMedicineName()));

        return medicine;
    }

    @Override
    public Medicine updateMedicine(String userId, String medicineId, MedicineRequest request) {
        Medicine medicine = getMedicineById(userId, medicineId);
        if (request.getMedicineName() != null) medicine.setMedicineName(request.getMedicineName());
        if (request.getDosage() != null) medicine.setDosage(request.getDosage());
        if (request.getFrequency() != null) medicine.setFrequency(request.getFrequency());
        if (request.getStartDate() != null) medicine.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) medicine.setEndDate(request.getEndDate());
        if (request.getNotes() != null) medicine.setNotes(request.getNotes());
        return medicineRepository.save(medicine);
    }

    @Override
    public void deleteMedicine(String userId, String medicineId) {
        Medicine medicine = getMedicineById(userId, medicineId);
        medicineRepository.delete(medicine);
    }

    @Override
    public Medicine getMedicineById(String userId, String medicineId) {
        return medicineRepository.findByIdAndUserId(medicineId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", medicineId));
    }

    @Override
    public List<Medicine> getUserMedicines(String userId, boolean activeOnly) {
        return medicineRepository.findByUserIdAndActiveOrderByCreatedAtDesc(userId, activeOnly);
    }

    @Override
    public void toggleMedicineActive(String userId, String medicineId, boolean active) {
        Medicine medicine = getMedicineById(userId, medicineId);
        medicine.setActive(active);
        medicineRepository.save(medicine);
        if (!active) {
            timelineService.autoCreateEvent(userId, "MEDICINE_STOPPED",
                    "Stopped " + medicine.getMedicineName(), null,
                    Map.of("medicineId", medicine.getId()));
        }
    }
}

