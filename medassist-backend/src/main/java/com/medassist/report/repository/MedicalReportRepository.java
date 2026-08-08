package com.medassist.report.repository;

import com.medassist.report.model.MedicalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalReportRepository extends MongoRepository<MedicalReport, String> {

    Page<MedicalReport> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<MedicalReport> findByUserIdAndStatus(String userId, String status);

    Optional<MedicalReport> findByIdAndUserId(String id, String userId);

    List<MedicalReport> findByUserIdAndReportTypeOrderByCreatedAtDesc(String userId, String reportType);

    long countByUserId(String userId);

    long countByStatus(String status);

    long countByCreatedAtAfter(LocalDateTime date);

    // For doctor access â€” returns reports shared by a patient
    @Query("{ 'user_id': ?0, 'status': 'COMPLETED' }")
    List<MedicalReport> findCompletedReportsByUserId(String userId);
}

