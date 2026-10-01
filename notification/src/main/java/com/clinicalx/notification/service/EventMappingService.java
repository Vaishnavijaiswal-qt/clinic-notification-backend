package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.EventMappingRequest;
import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.enums.NotificationType;
import com.clinicalx.notification.repository.EventMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventMappingService {

    private final EventMappingRepository repository;

    public List<EventMapping> create(EventMappingRequest request) {

        List<EventMapping> mappings = request.getMappings().stream()
                .flatMap(item -> item.getNotificationTypes().stream().map(notificationType -> {
                    if (repository.existsByClientIdAndClinicIdAndEventIdAndNotificationType(request.getClientId(),
                            request.getClinicId(), item.getEventId(), notificationType)) {

                        throw new IllegalArgumentException("Event mapping already exists for eventId: " + item.getEventId()
                                                + ", notificationType: " + notificationType);
                            }

                            EventMapping mapping = new EventMapping();

                            mapping.setClientId(request.getClientId());
                            mapping.setClinicId(request.getClinicId());
                            mapping.setEventId(item.getEventId());
                            mapping.setNotificationType(notificationType);

                            return mapping;})).toList();
        return repository.saveAll(mappings);
    }

    @Transactional(readOnly = true)
    public Page<EventMapping> get(
            Long clinicId,
            Long clientId,
            String search,
            int page) {

        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        if (search != null && search.isBlank()) {
            search = null;
        }

        if (search != null) {
            return repository.search(clinicId, clientId, search, pageable);
        }

        if (clinicId != null && clientId != null) {
            return repository.findByClinicIdAndClientId(clinicId, clientId, pageable);
        }

        if (clinicId != null) {
            return repository.findByClinicId(clinicId, pageable);
        }

        if (clientId != null) {
            return repository.findByClientId(clientId, pageable);
        }

        return repository.findAll(pageable);
    }

    public EventMapping update(Long id, EventMapping mapping) {

        boolean exists = repository.existsByClientIdAndClinicIdAndEventIdAndNotificationType(
                        mapping.getClientId(),
                        mapping.getClinicId(),
                        mapping.getEventId(),
                        mapping.getNotificationType()
                );

        if (exists) {
            throw new IllegalArgumentException("Event mapping already exists");
        }

        EventMapping existing = repository.getReferenceById(id);

        existing.setClientId(mapping.getClientId());
        existing.setClinicId(mapping.getClinicId());
        existing.setEventId(mapping.getEventId());
        existing.setNotificationType(mapping.getNotificationType());

        return repository.save(existing);
    }

    public String delete(Long id) {
        repository.deleteById(id);

        return "Event mapping deleted successfully";
    }
}