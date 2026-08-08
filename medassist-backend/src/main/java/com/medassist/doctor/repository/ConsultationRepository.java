package com.medassist.doctor.repository;

import com.medassist.doctor.model.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends MongoRepository<Consultation, String> {

    Page<Consultation> findByDoctorIdOrderByScheduledAtDesc(String doctorId, Pageable pageable);

    Page<Consultation> findByPatientIdOrderByScheduledAtDesc(String patientId, Pageable pageable);

    Optional<Consultation> findByIdAndDoctorId(String id, String doctorId);

    List<Consultation> findByDoctorIdAndStatus(String doctorId, String status);

    // Get all patient IDs for a doctor (for patient list)
    List<Consultation> findDistinctPatientIdByDoctorId(String doctorId);

    long countByDoctorId(String doctorId);
}

