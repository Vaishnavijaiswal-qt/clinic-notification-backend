package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventMappingRepository extends JpaRepository<EventMapping, Long> {

    Page<EventMapping> findByClientId(Long clientId, Pageable pageable);

    Page<EventMapping> findByClinicId(Long clinicId, Pageable pageable);

    Page<EventMapping> findByClinicIdAndClientId(Long clinicId, Long clientId, Pageable pageable);

    boolean existsByClientIdAndClinicIdAndEventIdAndNotificationType(Long clientId, Long clinicId, Long eventId, NotificationType notificationType);
}