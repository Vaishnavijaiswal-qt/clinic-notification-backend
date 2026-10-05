package com.clinicalx.notification.service;

import com.clinicalx.notification.entity.Template;
import com.clinicalx.notification.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.clinicalx.notification.enums.NotificationEvent;
import com.clinicalx.notification.enums.NotificationType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    // Get all templates
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    // Get templates for one event
    public List<Template> getTemplatesByEvent(
            com.clinicalx.notification.enums.NotificationEvent event) {

        return templateRepository.findByNotificationEvent(event);
    }

    // Get one template
    public Template getTemplateById(Long id) {

        return templateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Template not found with id: " + id));
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
    public Template getEmailTemplate(NotificationEvent event) {

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
}