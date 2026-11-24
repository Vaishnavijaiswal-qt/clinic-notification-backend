package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.TemplateItemResponse;
import com.clinicalx.notification.entity.NotificationEventConfig;
import com.clinicalx.notification.entity.Template;
import com.clinicalx.notification.repository.NotificationEventConfigRepository;
import com.clinicalx.notification.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.clinicalx.notification.enums.NotificationEvent;
import com.clinicalx.notification.enums.NotificationType;
import com.clinicalx.notification.entity.EventVariable;
import com.clinicalx.notification.repository.EventVariableRepository;
import com.clinicalx.notification.dto.GroupedTemplateResponse;
import java.util.LinkedHashMap;
import java.util.Map;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final EventVariableRepository eventVariableRepository;
    private final NotificationEventConfigRepository eventRepository;

    // Get all templates
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    // Get templates for one event
    public List<Template> getTemplatesByEvent(String event) {
        return templateRepository.findByNotificationEvent(event);
    }

    // Get one template
    public Template getTemplateById(Long id) {

        return templateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Template not found with id: " + id));
    }

    private void validateTemplateVariables(Template template) {

        // Find event using event name
        String eventKey = template.getNotificationEvent()
                .replace("_", " ")
                .trim();

        NotificationEventConfig event = null;

        List<NotificationEventConfig> events =
                eventRepository.findAll();

        for (NotificationEventConfig existingEvent : events) {

            if (existingEvent.getEventName()
                    .trim()
                    .equalsIgnoreCase(eventKey)) {

                event = existingEvent;
                break;
            }
        }

        if (event == null) {
            throw new RuntimeException("Notification event not found");
        }

        // Get active variables for this event
        List<EventVariable> registeredVariables =
                eventVariableRepository.findByEventIdAndActiveTrue(
                        event.getId()
                );

        // Store allowed variable keys
        List<String> allowedVariables = new ArrayList<>();

        for (EventVariable variable : registeredVariables) {
            allowedVariables.add(variable.getVariableKey());
        }

        // Combine subject and message
        String content = "";

        if (template.getSubject() != null) {
            content = content + template.getSubject();
        }

        if (template.getMessage() != null) {
            content = content + template.getMessage();
        }

        // Find placeholders
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {

            String variableKey = matcher.group(1).trim();

            if (!allowedVariables.contains(variableKey)) {
                throw new RuntimeException(
                        "Invalid template variable: " + variableKey
                );
            }
        }
    }

    // ADD TEMPLATE
    public Template addTemplate(Template template) {

        if (templateRepository
                .findByNotificationEventAndNotificationType(
                        template.getNotificationEvent(),
                        template.getNotificationType()
                ).isPresent()) {

            throw new RuntimeException(
                    "Template already exists for this event and notification type"
            );
        }

        validateTemplateVariables(template);

        return templateRepository.save(template);
    }

    // EDIT TEMPLATE
    public Template updateTemplate(
            Long id,
            Template templateDetails) {

        Template existingTemplate = templateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Template not found with id: " + id));

        existingTemplate.setNotificationEvent(
                templateDetails.getNotificationEvent()
        );

        existingTemplate.setNotificationType(
                templateDetails.getNotificationType()
        );

        existingTemplate.setSubject(
                templateDetails.getSubject()
        );

        existingTemplate.setMessage(
                templateDetails.getMessage()
        );

        validateTemplateVariables(existingTemplate);
        return templateRepository.save(existingTemplate);
    }

    // DELETE TEMPLATE
    public void deleteTemplate(Long id) {

        Template template = templateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Template not found with id: " + id));

        templateRepository.delete(template);
    }
    // Get EMAIL template for an event
    public Template getEmailTemplate(String event) {
        return templateRepository
                .findByNotificationEventAndNotificationType(
                        event,
                        NotificationType.EMAIL
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email template not found for event: " + event
                        )
                );
    }

    // Get all templates grouped by notification event
    public List<GroupedTemplateResponse> getGroupedTemplates() {

        List<Template> allTemplates = templateRepository.findAll();

        Map<String, List<TemplateItemResponse>> groupedMap =
                new LinkedHashMap<>();

        for (Template template : allTemplates) {

            String event = template.getNotificationEvent();

            if (!groupedMap.containsKey(event)) {
                groupedMap.put(event, new ArrayList<>());
            }

            TemplateItemResponse item = new TemplateItemResponse(
                    template.getId(),
                    template.getNotificationType(),
                    template.getSubject(),
                    template.getMessage(),
                    template.getCreatedAt(),
                    template.getUpdatedAt()
            );

            groupedMap.get(event).add(item);
        }

        List<GroupedTemplateResponse> response = new ArrayList<>();

        for (Map.Entry<String, List<TemplateItemResponse>> entry
                : groupedMap.entrySet()) {

            response.add(new GroupedTemplateResponse(
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        return response;
    }
}