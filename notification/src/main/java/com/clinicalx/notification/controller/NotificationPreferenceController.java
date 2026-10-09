package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.NotificationPreferenceResponse;
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

    private final NotificationPreferenceService notificationPreferenceService;;
    private final NotificationPreferenceService preferenceService;

    @PostMapping
    public NotificationPreferenceResponse savePreferences(
            @RequestBody NotificationPreferenceUpdateRequest request) {

        return preferenceService.savePreferences(request);
    }
    @GetMapping
    public NotificationPreferenceResponse getPreferences(
            @RequestParam Long clinicId) {

        return preferenceService.getPreferences(clinicId);
    }
}