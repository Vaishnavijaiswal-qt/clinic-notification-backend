
package com.clinicalx.notification.repository;

import com.clinicalx.notification.entity.EventVariable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventVariableRepository
        extends JpaRepository<EventVariable, Long> {

    List<EventVariable> findByEventIdAndActiveTrue(Long eventId);

    boolean existsByEventIdAndVariableKey(
            Long eventId,
            String variableKey
    );

    boolean existsByEventIdAndVariableKeyAndIdNot(
            Long eventId,
            String variableKey,
            Long id
    );
}
