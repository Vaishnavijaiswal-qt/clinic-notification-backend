
package com.clinicalx.notification.service;

import com.clinicalx.notification.entity.EventVariable;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.repository.EventVariableRepository;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventVariableService {

    private final EventVariableRepository eventVariableRepository;
    private final NotificationEventConfigRepository eventRepository;

    public EventVariable addVariable(Long eventId, EventVariable variable) {

        NotificationEventConfig event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Notification event not found"));

        if (eventVariableRepository.existsByEventIdAndVariableKey(
                eventId, variable.getVariableKey())) {
            throw new RuntimeException("Variable already exists for this event");
        }

        variable.setEvent(event);
        variable.setActive(true);

        return eventVariableRepository.save(variable);
    }

    public List<EventVariable> getVariables(Long eventId) {

        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Notification event not found");
        }

        return eventVariableRepository.findByEventIdAndActiveTrue(eventId);
    }

    public EventVariable updateVariable(Long id, EventVariable updatedVariable) {

        EventVariable existing = eventVariableRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Variable not found"));

        existing.setVariableKey(updatedVariable.getVariableKey());
        existing.setDisplayName(updatedVariable.getDisplayName());
        existing.setDataType(updatedVariable.getDataType());
        existing.setRequired(updatedVariable.isRequired());

        return eventVariableRepository.save(existing);
    }

    public void deleteVariable(Long id) {

        EventVariable existing = eventVariableRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Variable not found"));

        existing.setActive(false);
        eventVariableRepository.save(existing);
    }
}
