package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.EventMappingPageResponse;
import com.clinicalx.notification.dto.EventMappingRequest;
import com.clinicalx.notification.dto.EventMappingResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
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

    public List<EventMappingResponse> create(EventMappingRequest request) {

        List<EventMapping> mappings = new ArrayList<>();

        for (EventMappingRequest.EventMappingItem item : request.getMappings()) {

            for (NotificationType notificationType : item.getNotificationTypes()) {

                boolean exists =
                        repository.existsByClientIdAndClinicIdAndEventIdAndNotificationType(
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

        repository.saveAll(mappings);

        return buildResponse(mappings);

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

        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), mappings.size());

        List<EventMapping> pageMappings;

        if (start >= mappings.size()) {
            pageMappings = new ArrayList<>();
        } else {
            pageMappings = mappings.subList(start, end);
        }

        List<EventMappingResponse> responses =
                buildResponse(pageMappings);

        int totalElements = mappings.size();

        int totalPages = (int) Math.ceil(
                (double) totalElements / size
        );

        return new EventMappingPageResponse(
                responses,
                new EventMappingPageResponse.Pagination(
                        page,
                        size,
                        totalElements,
                        totalPages,
                        page == 0,
                        page >= totalPages - 1
                )
        );
    }

    public EventMappingResponse update(Long id, EventMapping mapping) {

        EventMapping existing = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event mapping not found"));

        existing.setClientId(mapping.getClientId());
        existing.setClinicId(mapping.getClinicId());
        existing.setEventId(mapping.getEventId());
        existing.setNotificationType(mapping.getNotificationType());

        EventMapping saved = repository.save(existing);

        return buildResponse(List.of(saved)).get(0);
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
}