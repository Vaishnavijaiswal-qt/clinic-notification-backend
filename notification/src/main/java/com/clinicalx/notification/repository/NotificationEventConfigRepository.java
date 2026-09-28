package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.NotificationEventConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationEventConfigRepository
        extends JpaRepository<NotificationEventConfig, Long> {

    List<NotificationEventConfig> findByOrderByCreatedAtAsc();

    List<NotificationEventConfig> findByEventNameContainingIgnoreCaseOrderByCreatedAtAsc(String eventName);

    boolean existsByEventName(String eventName);
    boolean existsByEventNameIgnoreCaseAndIdNot(String eventName, Long id);
}