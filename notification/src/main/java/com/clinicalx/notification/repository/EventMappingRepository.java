package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventMappingRepository extends JpaRepository<EventMapping, Long> {

    List<EventMapping> findByClientId(Long clientId);

    List<EventMapping> findByClinicId(Long clinicId);

    List<EventMapping> findByClinicIdAndClientId(
            Long clinicId,
            Long clientId
    );

    @Query("""
        SELECT em
        FROM EventMapping em
        WHERE
            (:clinicId IS NULL OR em.clinicId = :clinicId)
        AND
            (:clientId IS NULL OR em.clientId = :clientId)
        AND
            (
                :search IS NULL
                OR EXISTS (
                    SELECT c.id
                    FROM Client c
                    WHERE c.id = em.clientId
                    AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                OR EXISTS (
                    SELECT cl.id
                    FROM Clinic cl
                    WHERE cl.id = em.clinicId
                    AND LOWER(cl.name) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            )
        """)
    List<EventMapping> search(
            @Param("clinicId") Long clinicId,
            @Param("clientId") Long clientId,
            @Param("search") String search
    );

    boolean existsByClientIdAndClinicIdAndEventIdAndNotificationType(
            Long clientId,
            Long clinicId,
            Long eventId,
            NotificationType notificationType
    );
}