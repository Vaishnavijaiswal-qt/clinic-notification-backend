
package com.clinicalx.notification.service;

import com.clinicalx.notification.entity.EventVariable;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.repository.EventVariableRepository;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;
import com.clinicalx.notification.dto.EventVariableResponse;
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

    public EventVariableResponse getVariables(Long eventId) {

        NotificationEventConfig event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Notification event not found"));

        List<EventVariable> eventVariables =
                eventVariableRepository.findByEventIdAndActiveTrue(eventId);

        List<EventVariableResponse.VariableDetails> variableDetails =
                new java.util.ArrayList<>();

        for (EventVariable variable : eventVariables) {

            EventVariableResponse.VariableDetails details =
                    new EventVariableResponse.VariableDetails(
                            variable.getId(),
                            variable.getVariableKey(),
                            variable.getDisplayName(),
                            variable.getDataType(),
                            variable.isRequired()
                    );

            variableDetails.add(details);
        }

        return new EventVariableResponse(
                event.getId(),
                event.getEventName(),
                variableDetails
        );
    }

    public EventVariable updateVariable(Long id, EventVariable updatedVariable) {

        EventVariable existing = eventVariableRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Variable not found"));

        if (eventVariableRepository.existsByEventIdAndVariableKeyAndIdNot(
                existing.getEvent().getId(),
                updatedVariable.getVariableKey(),
                id)) {

            throw new IllegalArgumentException(
                    "Variable already exists for this event");
        }

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
