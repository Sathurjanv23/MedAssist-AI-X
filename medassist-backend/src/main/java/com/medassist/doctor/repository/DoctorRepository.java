package com.medassist.doctor.repository;

import com.medassist.doctor.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {

    Optional<Doctor> findByUserId(String userId);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    boolean existsByUserId(String userId);

    boolean existsByLicenseNumber(String licenseNumber);

    Page<Doctor> findByVerifiedStatus(String status, Pageable pageable);

    List<Doctor> findBySpecializationAndVerifiedStatus(String specialization, String status);
}

