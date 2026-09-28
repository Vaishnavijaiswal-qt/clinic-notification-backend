package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.NotificationEventCreateRequest;
import com.clinicalx.notification.dto.NotificationEventResponse;
import com.clinicalx.notification.dto.NotificationEventUpdateRequest;
import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationEventConfigService {

    private final NotificationEventConfigRepository repository;

    @Transactional(readOnly = true)
    public List<NotificationEventResponse> getEvents(String search) {

        List<NotificationEventConfig> events = (search == null || search.isBlank())
                        ? repository.findByOrderByCreatedAtAsc() : repository.findByEventNameContainingIgnoreCaseOrderByCreatedAtAsc(search.trim());

        return events.stream().map(this::toResponse).toList();
    }

    @Transactional
    public NotificationEventResponse createEvent(NotificationEventCreateRequest request) {

        if (repository.existsByEventName( request.eventName())) {
            throw new IllegalArgumentException("Event already exists ");
        }

        NotificationEventConfig event = new NotificationEventConfig();

        event.setEventName(request.eventName());
        event.setDescription(request.description());

        NotificationEventConfig saved = repository.save(event);
        return toResponse(saved);
    }

    @Transactional
    public NotificationEventResponse updateEvent(Long id, NotificationEventUpdateRequest request) {

        if (repository.existsByEventNameIgnoreCaseAndIdNot(request.eventName(), id)) {
            throw new IllegalArgumentException("Event already exists: " + request.eventName());
        }

        NotificationEventConfig event = repository.getReferenceById(id);

        event.setEventName(request.eventName());
        event.setDescription(request.description());

        return toResponse(repository.save(event));
    }

    @Transactional
    public void deleteEvent(Long id) {
        repository.deleteById(id);
    }

    private NotificationEventResponse toResponse(NotificationEventConfig event) {

        return new NotificationEventResponse(
                event.getId(),
                event.getEventName(),
                event.getDescription(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}