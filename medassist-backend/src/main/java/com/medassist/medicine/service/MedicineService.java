package com.medassist.medicine.service;

import com.medassist.medicine.dto.request.MedicineRequest;
import com.medassist.medicine.model.Medicine;

import java.util.List;

public interface MedicineService {
    Medicine addMedicine(String userId, MedicineRequest request);
    Medicine updateMedicine(String userId, String medicineId, MedicineRequest request);
    void deleteMedicine(String userId, String medicineId);
    Medicine getMedicineById(String userId, String medicineId);
    List<Medicine> getUserMedicines(String userId, boolean activeOnly);
    void toggleMedicineActive(String userId, String medicineId, boolean active);
}

