package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.*;
import com.infrastructure.monitoring.service.ProjectService;
import com.infrastructure.monitoring.service.RiskPredictionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final RiskPredictionService riskPredictionService;

    public ProjectController(ProjectService projectService, RiskPredictionService riskPredictionService) {
        this.projectService = projectService;
        this.riskPredictionService = riskPredictionService;
    }

    /**
     * List all projects with optional filtering by risk band and/or sector.
     * Roadmap Section 8.2: GET /api/projects (supports ?risk=HIGH&sector=Roads)
     */
    @GetMapping
    public List<ProjectResponseDTO> getAllProjects(
            @RequestParam(required = false) String risk,
            @RequestParam(required = false) String sector) {

        return projectService.getAllProjectResponses(risk, sector);
    }

    /**
     * Get single project details.
     * Roadmap Section 8.2: GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectResponseById(id));
    }

    /**
     * Create a new project (Admin only).
     * Roadmap Section 8.2: POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO created = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing project (Admin or assigned Officer).
     * Roadmap Section 8.2: PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO request) {

        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    /**
     * Delete a project (Admin only).
     * Roadmap Section 8.2: DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a physical-progress snapshot.
     * Roadmap Section 8.2: POST /api/projects/{id}/milestones
     */
    @PostMapping("/{id}/milestones")
    public ResponseEntity<MilestoneResponseDTO> addMilestone(
            @PathVariable Long id,
            @Valid @RequestBody MilestoneRequestDTO request) {

        MilestoneResponseDTO created = projectService.addMilestone(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Add a financial progress snapshot.
     * Roadmap Section 8.2: POST /api/projects/{id}/financials
     */
    @PostMapping("/{id}/financials")
    public ResponseEntity<FinancialRecordResponseDTO> addFinancialRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequestDTO request) {

        FinancialRecordResponseDTO created = projectService.addFinancialRecord(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get the latest risk assessment for a project.
     * Roadmap Section 8.2: GET /api/projects/{id}/risk
     */
    @GetMapping("/{id}/risk")
    public ResponseEntity<RiskAssessmentResponseDTO> getProjectRisk(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getLatestRisk(id));
    }

    /**
     * Force a new risk assessment evaluation.
     * Roadmap Section 8.2: POST /api/projects/{id}/risk/refresh
     */
    @PostMapping("/{id}/risk/refresh")
    public ResponseEntity<RiskAssessmentResponseDTO> refreshProjectRisk(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.refreshRisk(id));
    }

    /**
     * Trigger ML-based XGBoost Risk Prediction via FastAPI service.
     * POST /api/projects/{id}/risk-prediction
     */
    @PostMapping("/{id}/risk-prediction")
    public ResponseEntity<MlPredictionResponseDTO> predictProjectRisk(@PathVariable Long id) {
        return ResponseEntity.ok(riskPredictionService.predictProjectRisk(id));
    }
}