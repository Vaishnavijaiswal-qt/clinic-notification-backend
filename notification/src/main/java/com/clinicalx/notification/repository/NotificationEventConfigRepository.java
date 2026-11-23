package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.NotificationEventConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationEventConfigRepository
        extends JpaRepository<NotificationEventConfig, Long> {

    Page<NotificationEventConfig> findByEventNameContainingIgnoreCase(
            String eventName,
            Pageable pageable
    );

    boolean existsByEventNameIgnoreCase(String eventName);

    boolean existsByEventNameIgnoreCaseAndIdNot(
            String eventName,
            Long id
    );

    Optional<NotificationEventConfig> findByEventNameIgnoreCase(
            String eventName
    );
}