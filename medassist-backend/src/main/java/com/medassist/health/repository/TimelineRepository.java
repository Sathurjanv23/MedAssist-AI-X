package com.medassist.health.repository;

import com.medassist.health.model.HealthTimeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimelineRepository extends MongoRepository<HealthTimeline, String> {

    Page<HealthTimeline> findByUserIdOrderByEventDateDesc(String userId, Pageable pageable);

    List<HealthTimeline> findByUserIdAndEventDateBetweenOrderByEventDateDesc(
        String userId, LocalDate from, LocalDate to);

    List<HealthTimeline> findByUserIdAndEventTypeOrderByEventDateDesc(String userId, String eventType);

    List<HealthTimeline> findByUserIdAndMilestoneOrderByEventDateDesc(String userId, boolean milestone);
}

