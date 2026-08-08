package com.medassist.user.repository;

import com.medassist.user.model.MedicalProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalProfileRepository extends MongoRepository<MedicalProfile, String> {

    Optional<MedicalProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);

    void deleteByUserId(String userId);
}

