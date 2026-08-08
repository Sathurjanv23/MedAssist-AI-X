package com.medassist.medicine.repository;

import com.medassist.medicine.model.Medicine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends MongoRepository<Medicine, String> {

    List<Medicine> findByUserIdAndActiveOrderByCreatedAtDesc(String userId, boolean active);

    Optional<Medicine> findByIdAndUserId(String id, String userId);

    // Medicines ending soon (for refill reminders)
    @Query("{ 'user_id': ?0, 'is_active': true, 'end_date': { $gte: ?1, $lte: ?2 } }")
    List<Medicine> findMedicinesEndingSoon(String userId, LocalDate from, LocalDate to);

    // All medicines with reminder times (for scheduler)
    @Query("{ 'is_active': true, 'reminder_times': { $exists: true, $ne: [] } }")
    List<Medicine> findAllActiveWithReminders();

    long countByUserIdAndActive(String userId, boolean active);
}

