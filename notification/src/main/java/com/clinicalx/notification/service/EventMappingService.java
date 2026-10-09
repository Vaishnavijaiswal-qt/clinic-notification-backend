package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.*;
import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.enums.NotificationType;
import com.clinicalx.notification.repository.ClientRepository;
import com.clinicalx.notification.repository.ClinicRepository;
import com.clinicalx.notification.repository.EventMappingRepository;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EventMappingService {

    private final EventMappingRepository repository;
    private final ClientRepository clientRepository;
    private final ClinicRepository clinicRepository;
    private final NotificationEventConfigRepository eventRepository;

    public EventMappingCreateResponse create(EventMappingRequest request) {

        List<EventMapping> mappingsToSave = new ArrayList<>();

        for (EventMappingRequest.EventMappingItem item : request.getMappings()) {
            for (NotificationType notificationType : item.getNotificationTypes()) {

                boolean exists =
                        repository.existsByClientIdAndClinicIdAndEventIdAndNotificationType(
                                request.getClientId(),
                                request.getClinicId(),
                                item.getEventId(),
                                notificationType
                        );

                if (!exists) {
                    EventMapping mapping = new EventMapping();
                    mapping.setClientId(request.getClientId());
                    mapping.setClinicId(request.getClinicId());
                    mapping.setEventId(item.getEventId());
                    mapping.setNotificationType(notificationType);

                    mappingsToSave.add(mapping);
                }
            }
        }

        repository.saveAll(mappingsToSave);

        List<EventMapping> savedMappings = new ArrayList<>();

        for (EventMappingRequest.EventMappingItem item : request.getMappings()) {
            List<EventMapping> existingMappings =
                    repository.findByClientIdAndClinicIdAndEventId(
                            request.getClientId(),
                            request.getClinicId(),
                            item.getEventId()
                    );

            savedMappings.addAll(existingMappings);
        }

        return buildCreateResponse(request, savedMappings);
    }

    @Transactional(readOnly = true)
    public EventMappingPageResponse get(
            Long clinicId,
            Long clientId,
            String search,
            int page,
            int size) {

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero"
            );
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (search != null && search.isBlank()) {
            search = null;
        }

        List<EventMapping> mappings;

        if (search != null) {
            mappings = repository.search(
                    clinicId,
                    clientId,
                    search.trim()
            );
        } else if (clinicId != null && clientId != null) {
            mappings = repository.findByClinicIdAndClientId(
                    clinicId,
                    clientId
            );
        } else if (clinicId != null) {
            mappings = repository.findByClinicId(clinicId);
        } else if (clientId != null) {
            mappings = repository.findByClientId(clientId);
        } else {
            mappings = repository.findAll();
        }

        mappings.removeIf(
                mapping -> mapping.getNotificationType() == null
        );

        mappings.sort(Comparator.comparing(EventMapping::getId));

        List<EventMappingResponse> responses = buildResponse(mappings);

        int totalElements = responses.size();

        int totalPages = totalElements == 0
                ? 0
                : (int) Math.ceil((double) totalElements / size);

        int safePage = totalPages == 0
                ? 0
                : Math.min(page, totalPages - 1);

        int start = safePage * size;
        int end = Math.min(start + size, totalElements);

        List<EventMappingResponse> pageResponses =
                new ArrayList<>(responses.subList(start, end));

        return new EventMappingPageResponse(
                pageResponses,
                new EventMappingPageResponse.Pagination(
                        safePage,
                        size,
                        totalElements,
                        totalPages,
                        safePage == 0,
                        totalPages == 0 || safePage == totalPages - 1
                )
        );
    }

    public EventMappingUpdateResponse update(EventMappingRequest request) {

        List<EventMapping> existingMappings =
                repository.findByClientIdAndClinicIdAndEventId(
                        request.getClientId(),
                        request.getClinicId(),
                        request.getEventId()
                );

        List<NotificationType> requestedTypes =
                request.getNotificationTypes();

        for (EventMapping existing : existingMappings) {
            NotificationType existingType = existing.getNotificationType();

            if (existingType == null || !requestedTypes.contains(existingType)) {
                repository.delete(existing);
            }
        }

        for (NotificationType requestedType : requestedTypes) {
            boolean alreadyExists = false;

            for (EventMapping existing : existingMappings) {
                if (existing.getNotificationType() == requestedType) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                EventMapping mapping = new EventMapping();
                mapping.setClientId(request.getClientId());
                mapping.setClinicId(request.getClinicId());
                mapping.setEventId(request.getEventId());
                mapping.setNotificationType(requestedType);

                repository.save(mapping);
            }
        }

        List<String> notificationTypes = new ArrayList<>();

        for (NotificationType type : requestedTypes) {
            notificationTypes.add(type.name());
        }

        return new EventMappingUpdateResponse(
                request.getClientId(),
                request.getClinicId(),
                request.getEventId(),
                notificationTypes
        );
    }

    public String delete(Long id) {

        EventMapping mapping = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event mapping not found")
                );

        List<EventMapping> groupMappings =
                repository.findByClientIdAndClinicIdAndEventId(
                        mapping.getClientId(),
                        mapping.getClinicId(),
                        mapping.getEventId()
                );

        repository.deleteAll(groupMappings);

        return "Event mapping deleted successfully";
    }

    private EventMappingCreateResponse buildCreateResponse(
            EventMappingRequest request,
            List<EventMapping> mappings) {

        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Client not found")
                );

        var clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Clinic not found")
                );

        Map<Long, EventMappingCreateResponse.MappingItem> groupedEvents =
                new LinkedHashMap<>();

        mappings.stream()
                .filter(mapping -> mapping.getNotificationType() != null)
                .sorted(Comparator.comparing(EventMapping::getId))
                .forEach(mapping -> {

                    Long eventId = mapping.getEventId();

                    EventMappingCreateResponse.MappingItem item =
                            groupedEvents.get(eventId);

                    if (item == null) {
                        NotificationEventConfig event =
                                eventRepository.findById(eventId).orElse(null);

                        item = new EventMappingCreateResponse.MappingItem(
                                mapping.getId(),
                                eventId,
                                event == null ? null : event.getEventName(),
                                new ArrayList<>()
                        );

                        groupedEvents.put(eventId, item);
                    }

                    String type = mapping.getNotificationType().name();

                    if (!item.getNotificationTypes().contains(type)) {
                        item.getNotificationTypes().add(type);
                    }
                });

        return new EventMappingCreateResponse(
                client.getId(),
                client.getName(),
                clinic.getId(),
                clinic.getName(),
                new ArrayList<>(groupedEvents.values())
        );
    }

    private List<EventMappingResponse> buildResponse(
            List<EventMapping> mappings) {

        Map<String, EventMappingResponse> groupedEvents =
                new LinkedHashMap<>();

        for (EventMapping mapping : mappings) {
            if (mapping.getNotificationType() == null) {
                continue;
            }

            String key = mapping.getClientId()
                    + "-"
                    + mapping.getClinicId()
                    + "-"
                    + mapping.getEventId();

            EventMappingResponse response = groupedEvents.get(key);

            if (response == null) {
                var client = clientRepository.findById(mapping.getClientId())
                        .orElse(null);

                var clinic = clinicRepository.findById(mapping.getClinicId())
                        .orElse(null);

                NotificationEventConfig event =
                        eventRepository.findById(mapping.getEventId())
                                .orElse(null);

                response = new EventMappingResponse(
                        mapping.getId(),
                        mapping.getClientId(),
                        client == null ? null : client.getName(),
                        mapping.getClinicId(),
                        clinic == null ? null : clinic.getName(),
                        mapping.getEventId(),
                        event == null ? null : event.getEventName(),
                        new ArrayList<>()
                );

                groupedEvents.put(key, response);
            }

            String type = mapping.getNotificationType().name();

            if (!response.getNotificationTypes().contains(type)) {
                response.getNotificationTypes().add(type);
            }
        }

        return new ArrayList<>(groupedEvents.values());
    }
}
