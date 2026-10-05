package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.Template;
import com.clinicalx.notification.enums.NotificationEvent;
import com.clinicalx.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByNotificationEvent(
            NotificationEvent notificationEvent
    );

    Optional<Template> findByNotificationEventAndNotificationType(
            NotificationEvent notificationEvent,
            NotificationType notificationType
    );
}