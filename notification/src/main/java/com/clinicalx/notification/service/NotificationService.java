package com.clinicalx.notification.service;

import com.clinicalx.notification.entity.Template;
import com.clinicalx.notification.enums.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TemplateService templateService;
    private final EmailService emailService;

    public void sendEmailNotification(
            String to,
            NotificationEvent event,
            Map<String, String> variables) {

        // 1. Find the EMAIL template for this event
        Template template =
                templateService.getEmailTemplate(event);

        // 2. Get subject and message from the template
        String subject = template.getSubject();
        String message = template.getMessage();

        // 3. Replace variables such as {{patientName}}
        if (variables != null) {

            for (Map.Entry<String, String> entry : variables.entrySet()) {

                String placeholder =
                        "{{" + entry.getKey() + "}}";

                if (subject != null) {
                    subject = subject.replace(
                            placeholder,
                            entry.getValue()
                    );
                }

                if (message != null) {
                    message = message.replace(
                            placeholder,
                            entry.getValue()
                    );
                }
            }
        }

        // 4. Send the email
        emailService.sendEmail(
                to,
                subject,
                message
        );
    }
}