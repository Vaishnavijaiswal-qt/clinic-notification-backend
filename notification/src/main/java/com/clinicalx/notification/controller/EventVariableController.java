
package com.clinicalx.notification.controller;

import com.clinicalx.notification.entity.EventVariable;
import com.clinicalx.notification.service.EventVariableService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventVariableController {

    private final EventVariableService eventVariableService;

    // Add variable for an event
    @PostMapping("/notification-events/{eventId}/variables")
    public EventVariable addVariable(
            @PathVariable Long eventId,
            @RequestBody EventVariable variable) {

        return eventVariableService.addVariable(eventId, variable);
    }

    // Get active variables of an event
    @GetMapping("/notification-events/{eventId}/variables")
    public List<EventVariable> getVariables(
            @PathVariable Long eventId) {

        return eventVariableService.getVariables(eventId);
    }

    // Update variable
    @PutMapping("/event-variables/{id}")
    public EventVariable updateVariable(
            @PathVariable Long id,
            @RequestBody EventVariable variable) {

        return eventVariableService.updateVariable(id, variable);
    }

    // Delete variable (soft delete)
    @DeleteMapping("/event-variables/{id}")
    public String deleteVariable(@PathVariable Long id) {

        eventVariableService.deleteVariable(id);
        return "Variable deleted successfully";
    }
}
