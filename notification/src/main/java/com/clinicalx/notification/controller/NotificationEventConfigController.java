package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.NotificationEventCreateRequest;
import com.clinicalx.notification.dto.NotificationEventResponse;
import com.clinicalx.notification.dto.NotificationEventUpdateRequest;
import com.clinicalx.notification.service.NotificationEventConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-events")
@RequiredArgsConstructor
public class NotificationEventConfigController {

    private final NotificationEventConfigService service;

    @GetMapping
    public ResponseEntity<List<NotificationEventResponse>> getEvents(
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(service.getEvents(search));
    }

    @PostMapping
    public ResponseEntity<NotificationEventResponse> createEvent(
            @Valid @RequestBody NotificationEventCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createEvent(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationEventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody NotificationEventUpdateRequest request) {

        return ResponseEntity.ok(service.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {

        service.deleteEvent(id);
        return ResponseEntity.ok("Event deleted successfully");
    }
}