package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.NotificationPreferenceUpdateRequest;
import com.clinicalx.notification.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService service;

    @PostMapping
    public ResponseEntity<String> savePreferences(
            @Valid @RequestBody NotificationPreferenceUpdateRequest request) {

        service.savePreferences(request);

        return ResponseEntity.ok("Notification preferences saved successfully");
    }
}