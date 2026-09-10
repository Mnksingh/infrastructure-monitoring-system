package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.AlertResponseDTO;
import com.infrastructure.monitoring.entity.Alert;
import com.infrastructure.monitoring.service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * List alerts with optional resolved and severity filters.
     * Roadmap Section 8.2: GET /api/alerts?resolved=false
     */
    @GetMapping
    public List<AlertResponseDTO> getAlerts(
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String severity) {

        return alertService.getAlertResponses(resolved, severity);
    }

    /**
     * Get alert by ID.
     */
    @GetMapping("/{id}")
    public Alert getAlertById(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }

    /**
     * Create an alert manually.
     */
    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody Alert alert) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(alert));
    }

    /**
     * Mark an alert as resolved.
     * Roadmap Section 8.2: PATCH /api/alerts/{id}/resolve
     */
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<AlertResponseDTO> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.resolveAlert(id));
    }

    /**
     * Update an alert.
     */
    @PutMapping("/{id}")
    public Alert updateAlert(
            @PathVariable Long id,
            @RequestBody Alert alert) {

        return alertService.updateAlert(id, alert);
    }

    /**
     * Delete an alert.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}