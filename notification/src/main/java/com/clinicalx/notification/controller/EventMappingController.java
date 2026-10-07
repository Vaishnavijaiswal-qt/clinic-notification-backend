package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.EventMappingRequest;
import com.clinicalx.notification.dto.EventMappingResponse;
import com.clinicalx.notification.dto.EventMappingUpdateResponse;
import com.clinicalx.notification.entity.EventMapping;
import com.clinicalx.notification.service.EventMappingService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-mappings")
@RequiredArgsConstructor
public class EventMappingController {

    private final EventMappingService service;

    @GetMapping
    public ResponseEntity<?> get(
            @RequestParam(required = false) Long clinicId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.get(
                        clinicId,
                        clientId,
                        search,
                        page,
                        size
                )
        );
    }
    @PostMapping
    public ResponseEntity<List<EventMappingResponse>> create(
            @RequestBody EventMappingRequest request) {

        return ResponseEntity.ok(
                service.create(request)
        );
    }

    @PutMapping
    public ResponseEntity<EventMappingUpdateResponse> update(
            @RequestBody EventMappingRequest request) {

        return ResponseEntity.ok(
                service.update(request)
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.delete(id)
        );
    }
}