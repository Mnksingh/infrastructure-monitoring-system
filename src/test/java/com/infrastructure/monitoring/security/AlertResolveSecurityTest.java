package com.infrastructure.monitoring.security;

import com.infrastructure.monitoring.dto.AlertResponseDTO;
import com.infrastructure.monitoring.service.AlertService;
import com.infrastructure.monitoring.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import jakarta.servlet.Filter;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AlertResolveSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void testOptionsPreflightResolveEndpointAllowed() throws Exception {
        mockMvc.perform(options("/api/alerts/1/resolve")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "PATCH")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PATCH")));
    }

    @Test
    void testResolveAlertUnauthenticatedReturnsForbidden() throws Exception {
        mockMvc.perform(patch("/api/alerts/1/resolve"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testResolveAlertWithViewerRoleReturnsForbidden() throws Exception {
        String viewerToken = jwtService.generateToken("viewerUser", "VIEWER");

        mockMvc.perform(patch("/api/alerts/1/resolve")
                        .header("Authorization", "Bearer " + viewerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testResolveAlertWithOfficerRoleAllowed() throws Exception {
        String officerToken = jwtService.generateToken("officerUser", "OFFICER");
        AlertResponseDTO mockResponse = new AlertResponseDTO(
                1L, 1L, "Test Project", 1L, "HIGH", "Schedule delay", LocalDateTime.now(), true, LocalDateTime.now()
        );
        when(alertService.resolveAlert(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(patch("/api/alerts/1/resolve")
                        .header("Authorization", "Bearer " + officerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testResolveAlertWithAdminRoleAllowed() throws Exception {
        String adminToken = jwtService.generateToken("adminUser", "ADMIN");
        AlertResponseDTO mockResponse = new AlertResponseDTO(
                1L, 1L, "Test Project", 1L, "HIGH", "Schedule delay", LocalDateTime.now(), true, LocalDateTime.now()
        );
        when(alertService.resolveAlert(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(patch("/api/alerts/1/resolve")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
