package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.EmailNotificationRequest;
import com.clinicalx.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailNotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(
            @RequestBody EmailNotificationRequest request) {

        notificationService.sendEmailNotification(
                request.getTo(),
                request.getNotificationEvent(),
                request.getVariables()
        );

        return ResponseEntity.ok(
                "Email sent successfully"
        );
    }
}