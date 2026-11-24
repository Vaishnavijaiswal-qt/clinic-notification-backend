package com.clinicalx.notification.controller;

import com.clinicalx.notification.dto.GroupedTemplateResponse;
import com.clinicalx.notification.entity.Template;
import com.clinicalx.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    // GET ALL - GROUPED BY EVENT
    @GetMapping
    public ResponseEntity<List<GroupedTemplateResponse>> getAllTemplates() {

        return ResponseEntity.ok(
                templateService.getGroupedTemplates()
        );
    }

    // GET BY EVENT
    @GetMapping("/event/{event}")
    public ResponseEntity<List<Template>> getTemplatesByEvent(
            @PathVariable String event) {

        return ResponseEntity.ok(
                templateService.getTemplatesByEvent(event)
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplateById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                templateService.getTemplateById(id)
        );
    }

    // ADD
    @PostMapping
    public ResponseEntity<Template> addTemplate(
            @RequestBody Template template) {

        return ResponseEntity.ok(
                templateService.addTemplate(template)
        );
    }

    // EDIT
    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(
            @PathVariable Long id,
            @RequestBody Template template) {

        return ResponseEntity.ok(
                templateService.updateTemplate(id, template)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTemplate(
            @PathVariable Long id) {

        templateService.deleteTemplate(id);

        return ResponseEntity.ok(
                "Template deleted successfully"
        );
    }
}