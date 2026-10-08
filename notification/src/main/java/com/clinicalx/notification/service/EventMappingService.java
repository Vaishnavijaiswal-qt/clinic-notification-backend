package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.*;
import com.clinicalx.notification.entity.Client;
import com.clinicalx.notification.entity.Clinic;
import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.enums.NotificationType;
import com.clinicalx.notification.repository.ClientRepository;
import com.clinicalx.notification.repository.ClinicRepository;
import com.clinicalx.notification.repository.EventMappingRepository;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

        List<EventMapping> mappings = new ArrayList<>();

        for (EventMappingRequest.EventMappingItem item
                : request.getMappings()) {

            for (NotificationType notificationType
                    : item.getNotificationTypes()) {

                boolean exists =
                        repository
                                .existsByClientIdAndClinicIdAndEventIdAndNotificationType(
                                        request.getClientId(),
                                        request.getClinicId(),
                                        item.getEventId(),
                                        notificationType
                                );

                if (exists) {
                    continue;
                }

                EventMapping mapping = new EventMapping();

                mapping.setClientId(request.getClientId());
                mapping.setClinicId(request.getClinicId());
                mapping.setEventId(item.getEventId());
                mapping.setNotificationType(notificationType);

                mappings.add(mapping);
            }
        }

        repository.saveAll(mappings);

        // Get all mappings for this client + clinic
        List<EventMapping> allMappings =
                repository.findByClinicIdAndClientId(
                        request.getClinicId(),
                        request.getClientId()
                );

        return buildCreateResponse(mappings);
    }
    @Transactional(readOnly = true)
    public EventMappingPageResponse get(
            Long clinicId,
            Long clientId,
            String search,
            int page,
            int size) {

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

        Map<String, List<EventMapping>> groupedMap =
                new LinkedHashMap<>();

        for (EventMapping mapping : mappings) {

            String key =
                    mapping.getClientId()
                            + "-"
                            + mapping.getClinicId()
                            + "-"
                            + mapping.getEventId();

            if (!groupedMap.containsKey(key)) {
                groupedMap.put(
                        key,
                        new ArrayList<>()
                );
            }

            groupedMap.get(key).add(mapping);
        }

        List<List<EventMapping>> groupedMappings =
                new ArrayList<>(
                        groupedMap.values()
                );

        int totalElements =
                groupedMappings.size();

        int totalPages =
                totalElements == 0
                        ? 0
                        : (int) Math.ceil(
                        (double) totalElements / size
                );

        int safePage =
                totalPages == 0
                        ? 0
                        : Math.min(
                        Math.max(page, 0),
                        totalPages - 1
                );

        int start =
                safePage * size;

        int end =
                Math.min(
                        start + size,
                        totalElements
                );

        List<EventMapping> pageMappings =
                new ArrayList<>();

        for (int i = start; i < end; i++) {
            pageMappings.addAll(
                    groupedMappings.get(i)
            );
        }

        List<EventMappingResponse> responses =
                buildResponse(pageMappings);

        return new EventMappingPageResponse(
                responses,
                new EventMappingPageResponse.Pagination(
                        safePage,
                        size,
                        totalElements,
                        totalPages,
                        safePage == 0,
                        totalPages == 0 ||
                                safePage >= totalPages - 1
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

            NotificationType existingType =
                    existing.getNotificationType();

            if (!requestedTypes.contains(existingType)) {
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

        List<String> notificationTypes =
                new ArrayList<>();

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

        repository.deleteById(id);

        return "Event mapping deleted successfully";
    }

    private List<EventMappingResponse> buildResponse(
            List<EventMapping> mappings) {

        Map<Long, String> clientNames = new HashMap<>();
        Map<Long, String> clinicNames = new HashMap<>();
        Map<Long, String> eventNames = new HashMap<>();

        List<Long> clientIds = new ArrayList<>();
        List<Long> clinicIds = new ArrayList<>();
        List<Long> eventIds = new ArrayList<>();

        for (EventMapping mapping : mappings) {

            if (!clientIds.contains(mapping.getClientId())) {
                clientIds.add(mapping.getClientId());
            }

            if (!clinicIds.contains(mapping.getClinicId())) {
                clinicIds.add(mapping.getClinicId());
            }

            if (!eventIds.contains(mapping.getEventId())) {
                eventIds.add(mapping.getEventId());
            }
        }

        List<Client> clients =
                clientRepository.findAllById(clientIds);

        for (Client client : clients) {
            clientNames.put(
                    client.getId(),
                    client.getName()
            );
        }

        List<Clinic> clinics =
                clinicRepository.findAllById(clinicIds);

        for (Clinic clinic : clinics) {
            clinicNames.put(
                    clinic.getId(),
                    clinic.getName()
            );
        }

        List<NotificationEventConfig> events =
                eventRepository.findAllById(eventIds);

        for (NotificationEventConfig event : events) {
            eventNames.put(
                    event.getId(),
                    event.getEventName()
            );
        }

        List<EventMappingResponse> responseList =
                new ArrayList<>();

        for (EventMapping mapping : mappings) {

            EventMappingResponse response =
                    new EventMappingResponse();

            response.setId(mapping.getId());

            response.setClientId(
                    mapping.getClientId()
            );

            response.setClientName(
                    clientNames.get(mapping.getClientId())
            );

            response.setClinicId(
                    mapping.getClinicId()
            );

            response.setClinicName(
                    clinicNames.get(mapping.getClinicId())
            );

            response.setEventId(
                    mapping.getEventId()
            );

            response.setEventName(
                    eventNames.get(mapping.getEventId())
            );

            response.setNotificationType(
                    mapping.getNotificationType().name()
            );

            responseList.add(response);
        }

        return responseList;
    }
    private EventMappingCreateResponse buildCreateResponse(
            List<EventMapping> mappings) {

        if (mappings == null || mappings.isEmpty()) {
            return null;
        }

        Long clientId = mappings.get(0).getClientId();
        Long clinicId = mappings.get(0).getClinicId();

        String clientName =
                clientRepository.findById(clientId)
                        .map(Client::getName)
                        .orElse(null);

        String clinicName =
                clinicRepository.findById(clinicId)
                        .map(Clinic::getName)
                        .orElse(null);

        // Group notification types by event
        Map<Long, List<NotificationType>> groupedEvents =
                new LinkedHashMap<>();

        for (EventMapping mapping : mappings) {

            groupedEvents
                    .computeIfAbsent(
                            mapping.getEventId(),
                            key -> new ArrayList<>()
                    )
                    .add(mapping.getNotificationType());
        }

        // Get event names
        List<Long> eventIds =
                new ArrayList<>(groupedEvents.keySet());

        List<NotificationEventConfig> events =
                eventRepository.findAllById(eventIds);

        Map<Long, String> eventNames =
                new HashMap<>();

        for (NotificationEventConfig event : events) {
            eventNames.put(
                    event.getId(),
                    event.getEventName()
            );
        }

        // Build nested response
        List<EventMappingCreateResponse.EventMappingItemResponse> responseMappings =
                new ArrayList<>();

        for (Map.Entry<Long, List<NotificationType>> entry
                : groupedEvents.entrySet()) {

            Long eventId = entry.getKey();

            responseMappings.add(
                    new EventMappingCreateResponse.EventMappingItemResponse(
                            eventId,
                            eventNames.get(eventId),
                            entry.getValue()
                    )
            );
        }

        return new EventMappingCreateResponse(
                clientId,
                clientName,
                clinicId,
                clinicName,
                responseMappings
        );
    }
}