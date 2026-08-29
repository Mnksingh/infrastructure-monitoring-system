package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.entity.RiskAssessment;
import com.infrastructure.monitoring.service.RiskAssessmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-assessments")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    public RiskAssessmentController(RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    @GetMapping
    public List<RiskAssessment> getAllRiskAssessments() {
        return riskAssessmentService.getAllRiskAssessments();
    }

    @GetMapping("/{id}")
    public RiskAssessment getRiskAssessmentById(@PathVariable Long id) {
        return riskAssessmentService.getRiskAssessmentById(id);
    }

    @PostMapping
    public RiskAssessment createRiskAssessment(
            @RequestBody RiskAssessment riskAssessment) {
        return riskAssessmentService.createRiskAssessment(riskAssessment);
    }

    @PutMapping("/{id}")
    public RiskAssessment updateRiskAssessment(
            @PathVariable Long id,
            @RequestBody RiskAssessment riskAssessment) {

        return riskAssessmentService.updateRiskAssessment(id, riskAssessment);
    }

    @DeleteMapping("/{id}")
    public void deleteRiskAssessment(@PathVariable Long id) {
        riskAssessmentService.deleteRiskAssessment(id);
    }
}