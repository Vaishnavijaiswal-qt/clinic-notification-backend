package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventMappingRepository extends JpaRepository<EventMapping, Long> {

    Page<EventMapping> findByClientId(Long clientId, Pageable pageable);

    Page<EventMapping> findByClinicId(Long clinicId, Pageable pageable);

    Page<EventMapping> findByClinicIdAndClientId(
            Long clinicId,
            Long clientId,
            Pageable pageable
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
    Page<EventMapping> search(
            @Param("clinicId") Long clinicId,
            @Param("clientId") Long clientId,
            @Param("search") String search,
            Pageable pageable
    );

    boolean existsByClientIdAndClinicIdAndEventIdAndNotificationType(
            Long clientId,
            Long clinicId,
            Long eventId,
            NotificationType notificationType
    );
}