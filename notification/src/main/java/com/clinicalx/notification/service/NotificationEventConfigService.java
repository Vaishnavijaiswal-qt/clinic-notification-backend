package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.NotificationEventCreateRequest;
import com.clinicalx.notification.dto.NotificationEventResponse;
import com.clinicalx.notification.dto.NotificationEventUpdateRequest;
import com.clinicalx.notification.dto.PaginationResponse;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationEventConfigService {

    private final NotificationEventConfigRepository repository;

    @Transactional(readOnly = true)
    public PaginationResponse<NotificationEventResponse> getEvents(
            String search,
            int page,
            int size) {

        // If page is negative, use page 0
        if (page < 0) {
            page = 0;
        }

        // If size is 0 or negative, use 10
        if (size <= 0) {
            size = 10;
        }

        // Newest event first
        Sort sort = Sort.by(
                Sort.Direction.DESC,
                "createdAt"
        ).and(
                Sort.by(
                        Sort.Direction.DESC,
                        "id"
                )
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NotificationEventConfig> events;

        // Without search
        if (search == null || search.isBlank()) {

            events = repository.findAll(pageable);

        } else {

            // With search
            events = repository.findByEventNameContainingIgnoreCase(
                    search.trim(),
                    pageable
            );
        }

        // Convert Entity list to Response list
        // without using Stream
        List<NotificationEventResponse> responseList =
                new ArrayList<>();

        for (NotificationEventConfig event : events.getContent()) {

            responseList.add(toResponse(event));
        }

        return new PaginationResponse<>(
                responseList,
                events.getNumber(),
                events.getSize(),
                events.getTotalElements(),
                events.getTotalPages(),
                events.isFirst(),
                events.isLast()
        );
    }

    @Transactional
    public NotificationEventResponse createEvent(
            NotificationEventCreateRequest request) {

        // Check duplicate event name ignoring case
        if (repository.existsByEventNameIgnoreCase(
                request.eventName())) {

            throw new IllegalArgumentException(
                    "Event already exists: "
                            + request.eventName()
            );
        }

        NotificationEventConfig event =
                new NotificationEventConfig();

        event.setEventName(request.eventName());
        event.setDescription(request.description());

        NotificationEventConfig saved =
                repository.save(event);

        return toResponse(saved);
    }

    @Transactional
    public NotificationEventResponse updateEvent(
            Long id,
            NotificationEventUpdateRequest request) {

        // Check duplicate event name ignoring case
        // excluding the current event
        if (repository.existsByEventNameIgnoreCaseAndIdNot(
                request.eventName(),
                id)) {

            throw new IllegalArgumentException(
                    "Event already exists: "
                            + request.eventName()
            );
        }

        NotificationEventConfig event =
                repository.getReferenceById(id);

        event.setEventName(request.eventName());
        event.setDescription(request.description());

        NotificationEventConfig updated =
                repository.save(event);

        return toResponse(updated);
    }

    @Transactional
    public void deleteEvent(Long id) {

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Notification event not found with id: " + id
            );
        }

        repository.deleteById(id);
    }

    private NotificationEventResponse toResponse(
            NotificationEventConfig event) {

        return new NotificationEventResponse(
                event.getId(),
                event.getEventName(),
                event.getDescription(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}