package com.medassist.health.service.impl;

import com.medassist.common.exception.ResourceNotFoundException;
import com.medassist.health.model.HealthTimeline;
import com.medassist.health.repository.TimelineRepository;
import com.medassist.health.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {

    private final TimelineRepository timelineRepository;

    @Override
    public HealthTimeline addEvent(String userId, HealthTimeline event) {
        event.setUserId(userId);
        if (event.getEventDate() == null) event.setEventDate(LocalDate.now());
        if (event.getCreatedAt() == null) event.setCreatedAt(LocalDateTime.now());
        return timelineRepository.save(event);
    }

    @Override
    public Page<HealthTimeline> getUserTimeline(String userId, Pageable pageable) {
        return timelineRepository.findByUserIdOrderByEventDateDesc(userId, pageable);
    }

    @Override
    public void deleteEvent(String userId, String eventId) {
        HealthTimeline event = timelineRepository.findById(eventId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Timeline event", "id", eventId));
        timelineRepository.delete(event);
    }

    @Override
    public void autoCreateEvent(String userId, String eventType, String title,
                                String description, Map<String, Object> metadata) {
        HealthTimeline event = HealthTimeline.builder()
                .userId(userId)
                .eventType(eventType)
                .title(title)
                .description(description)
                .metadata(metadata)
                .eventDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
        timelineRepository.save(event);
    }
}

