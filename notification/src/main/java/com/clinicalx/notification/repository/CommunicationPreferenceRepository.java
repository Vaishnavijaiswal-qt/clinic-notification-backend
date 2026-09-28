package com.clinicalx.notification.repository;
import com.clinicalx.notification.entity.CommunicationPreference;
import com.clinicalx.notification.enums.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommunicationPreferenceRepository extends JpaRepository<CommunicationPreference, Long> {

    Optional<CommunicationPreference> findByClinicIdAndNotificationEvent(Long clinicId, NotificationEvent notificationEvent);

    List<CommunicationPreference> findByClinicId(Long clinicId);
}