package com.medassist.health.service;

import com.medassist.health.model.HealthTimeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimelineService {
    HealthTimeline addEvent(String userId, HealthTimeline event);
    Page<HealthTimeline> getUserTimeline(String userId, Pageable pageable);
    void deleteEvent(String userId, String eventId);
    void autoCreateEvent(String userId, String eventType, String title, String description, java.util.Map<String, Object> metadata);
}

