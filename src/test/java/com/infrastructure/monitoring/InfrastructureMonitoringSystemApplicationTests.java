package com.infrastructure.monitoring;

import com.infrastructure.monitoring.controller.ProjectController;
import com.infrastructure.monitoring.dto.MlPredictionResponseDTO;
import com.infrastructure.monitoring.dto.MlProjectFeaturesDTO;
import com.infrastructure.monitoring.entity.FinancialRecord;
import com.infrastructure.monitoring.entity.Milestone;
import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.exception.GlobalExceptionHandler;
import com.infrastructure.monitoring.repository.FinancialRecordRepository;
import com.infrastructure.monitoring.repository.MilestoneRepository;
import com.infrastructure.monitoring.repository.ProjectRepository;
import com.infrastructure.monitoring.service.ProjectService;
import com.infrastructure.monitoring.service.RiskPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class InfrastructureMonitoringSystemApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Autowired
    private RiskPredictionService riskPredictionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setup() {
        ProjectController controller = new ProjectController(projectService, riskPredictionService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void testRiskPredictionEndToEndWithRealProject() {
        Long projectId = 1L;
        Project project = projectRepository.findById(projectId).orElse(null);
        assertNotNull(project, "Project 1 should exist in database");

        List<Milestone> milestones = milestoneRepository.findByProjectIdOrderByReportedDateDesc(projectId);
        List<FinancialRecord> financials = financialRecordRepository.findByProjectIdOrderByReportedDateDesc(projectId);

        MlProjectFeaturesDTO features = riskPredictionService.extractFeatures(project, milestones, financials);
        System.out.println("=== EXTRACTED 21 ML FEATURES FOR PROJECT 1 ===");
        System.out.println(features);

        // Verify feature constraints
        assertTrue(features.physicalProgressPct() >= 0.0 && features.physicalProgressPct() <= 100.0);
        assertTrue(features.recentProgressStalled() == 0 || features.recentProgressStalled() == 1);
        assertTrue(features.isFirstReport() == 0 || features.isFirstReport() == 1);
        assertTrue(features.isOverdue() == 0 || features.isOverdue() == 1);
        assertTrue(features.isMegaProject() == 0 || features.isMegaProject() == 1);
        assertTrue(features.monthNum() >= 1 && features.monthNum() <= 12);
        assertTrue(features.agencyFreq() >= 1.0);

        // Call live FastAPI /predict via RiskPredictionService if available
        try {
            MlPredictionResponseDTO prediction = riskPredictionService.predictProjectRisk(projectId);
            System.out.println("=== FASTAPI PREDICTION RESPONSE ===");
            System.out.println("Risk Probability: " + prediction.riskProbability());
            System.out.println("Risk: " + prediction.risk());
            System.out.println("Risk Level: " + prediction.riskLevel());
            System.out.println("Threshold: " + prediction.threshold());
            System.out.println("Model Version: " + prediction.modelVersion());

            assertNotNull(prediction);
            assertEquals("v2", prediction.modelVersion());
            assertEquals(0.55, prediction.threshold(), 0.001);
            assertTrue(prediction.riskProbability() >= 0.0 && prediction.riskProbability() <= 1.0);
            assertTrue(List.of("LOW", "MEDIUM", "HIGH").contains(prediction.riskLevel()));
        } catch (com.infrastructure.monitoring.exception.MlClientException e) {
            System.out.println("FastAPI service is offline during test run: " + e.getMessage());
        }
    }

    @Test
    void testRiskPredictionHttpEndpointSuccess() throws Exception {
        mockMvc.perform(post("/api/projects/1/risk-prediction")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(org.hamcrest.Matchers.isOneOf(200, 503)));
    }

    @Test
    void testRiskPredictionHttpEndpointNotFound() throws Exception {
        mockMvc.perform(post("/api/projects/99999/risk-prediction")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Project not found with id: 99999"));
    }

}

