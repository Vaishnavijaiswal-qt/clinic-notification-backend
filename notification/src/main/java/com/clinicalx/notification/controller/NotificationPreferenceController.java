package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.NotificationPreferenceUpdateRequest;
import com.clinicalx.notification.entity.CommunicationPreference;
import com.clinicalx.notification.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService service;

    @PostMapping
    public ResponseEntity<List<CommunicationPreference>> savePreferences(
            @Valid @RequestBody NotificationPreferenceUpdateRequest request) {

        return ResponseEntity.ok(service.savePreferences(request));
    }
}