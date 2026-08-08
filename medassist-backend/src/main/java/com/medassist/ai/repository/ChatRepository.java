package com.medassist.ai.repository;

import com.medassist.ai.model.AiChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<AiChat, String> {

    Page<AiChat> findByUserIdAndActiveOrderByCreatedAtDesc(String userId, boolean active, Pageable pageable);

    Optional<AiChat> findByIdAndUserId(String id, String userId);

    Optional<AiChat> findFirstByUserIdAndActiveOrderByLastMessageAtDesc(String userId, boolean active);

    long countByUserId(String userId);
}

