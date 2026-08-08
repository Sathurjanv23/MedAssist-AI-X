package com.medassist.health.repository;

import com.medassist.health.model.HealthData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthDataRepository extends MongoRepository<HealthData, String> {

    Optional<HealthData> findByUserId(String userId);

    boolean existsByUserId(String userId);
}

