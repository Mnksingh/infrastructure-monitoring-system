package com.infrastructure.monitoring.security;

import com.infrastructure.monitoring.dto.RiskAssessmentResponseDTO;
import com.infrastructure.monitoring.service.JwtService;
import com.infrastructure.monitoring.service.ProjectService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProjectSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void testOptionsPreflightDeleteProjectAllowed() throws Exception {
        mockMvc.perform(options("/api/projects/1")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "DELETE")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("DELETE")));
    }

    @Test
    void testDeleteProjectUnauthenticatedReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteProjectWithViewerRoleReturnsForbidden() throws Exception {
        String viewerToken = jwtService.generateToken("viewerUser", "VIEWER");

        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", "Bearer " + viewerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteProjectWithOfficerRoleReturnsForbidden() throws Exception {
        String officerToken = jwtService.generateToken("officerUser", "OFFICER");

        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", "Bearer " + officerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteProjectWithAdminRoleAllowed() throws Exception {
        String adminToken = jwtService.generateToken("adminUser", "ADMIN");
        doNothing().when(projectService).deleteProject(eq(1L));

        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    // --- Tests for GET /api/projects/{id}/risk ---

    @Test
    void testGetProjectRiskWithAdminRoleAllowed() throws Exception {
        String adminToken = jwtService.generateToken("adminUser", "ADMIN");
        RiskAssessmentResponseDTO mockRisk = new RiskAssessmentResponseDTO(
                1L, 1L, LocalDateTime.now(), BigDecimal.valueOf(0.65), "HIGH", "Cost overrun", "Review timeline"
        );
        when(projectService.getLatestRisk(eq(1L))).thenReturn(mockRisk);

        mockMvc.perform(get("/api/projects/1/risk")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProjectRiskWithAssignedOfficerAllowed() throws Exception {
        String officerToken = jwtService.generateToken("assignedOfficer", "OFFICER");
        RiskAssessmentResponseDTO mockRisk = new RiskAssessmentResponseDTO(
                1L, 1L, LocalDateTime.now(), BigDecimal.valueOf(0.35), "LOW", "On track", "Maintain pace"
        );
        when(projectService.getLatestRisk(eq(1L))).thenReturn(mockRisk);

        mockMvc.perform(get("/api/projects/1/risk")
                        .header("Authorization", "Bearer " + officerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProjectRiskWithUnassignedOfficerReturns403() throws Exception {
        String unassignedOfficerToken = jwtService.generateToken("unassignedOfficer", "OFFICER");
        when(projectService.getLatestRisk(eq(1L)))
                .thenThrow(new AccessDeniedException("You are not assigned to this project"));

        mockMvc.perform(get("/api/projects/1/risk")
                        .header("Authorization", "Bearer " + unassignedOfficerToken))
                .andExpect(status().isForbidden());
    }

    // --- Tests for POST /api/projects/{id}/risk/refresh ---

    @Test
    void testRefreshProjectRiskWithAdminRoleAllowed() throws Exception {
        String adminToken = jwtService.generateToken("adminUser", "ADMIN");
        RiskAssessmentResponseDTO mockRisk = new RiskAssessmentResponseDTO(
                1L, 1L, LocalDateTime.now(), BigDecimal.valueOf(0.65), "HIGH", "Cost overrun", "Review timeline"
        );
        when(projectService.refreshRisk(eq(1L))).thenReturn(mockRisk);

        mockMvc.perform(post("/api/projects/1/risk/refresh")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testRefreshProjectRiskWithAssignedOfficerAllowed() throws Exception {
        String officerToken = jwtService.generateToken("assignedOfficer", "OFFICER");
        RiskAssessmentResponseDTO mockRisk = new RiskAssessmentResponseDTO(
                1L, 1L, LocalDateTime.now(), BigDecimal.valueOf(0.35), "LOW", "On track", "Maintain pace"
        );
        when(projectService.refreshRisk(eq(1L))).thenReturn(mockRisk);

        mockMvc.perform(post("/api/projects/1/risk/refresh")
                        .header("Authorization", "Bearer " + officerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testRefreshProjectRiskWithUnassignedOfficerReturns403() throws Exception {
        String unassignedOfficerToken = jwtService.generateToken("unassignedOfficer", "OFFICER");
        when(projectService.refreshRisk(eq(1L)))
                .thenThrow(new AccessDeniedException("You are not assigned to this project"));

        mockMvc.perform(post("/api/projects/1/risk/refresh")
                        .header("Authorization", "Bearer " + unassignedOfficerToken))
                .andExpect(status().isForbidden());
    }
}
