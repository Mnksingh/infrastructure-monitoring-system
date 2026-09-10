package com.infrastructure.monitoring.security;

import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.entity.User;
import com.infrastructure.monitoring.repository.*;
import com.infrastructure.monitoring.service.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ProjectServiceRiskOwnershipTest {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private MinistryRepository ministryRepository;
    private MilestoneRepository milestoneRepository;
    private FinancialRecordRepository financialRecordRepository;
    private RiskAssessmentRepository riskAssessmentRepository;
    private AlertRepository alertRepository;
    private PasswordEncoder passwordEncoder;

    private ProjectService projectService;
    private Project project;

    @BeforeEach
    void setUp() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        ministryRepository = Mockito.mock(MinistryRepository.class);
        milestoneRepository = Mockito.mock(MilestoneRepository.class);
        financialRecordRepository = Mockito.mock(FinancialRecordRepository.class);
        riskAssessmentRepository = Mockito.mock(RiskAssessmentRepository.class);
        alertRepository = Mockito.mock(AlertRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        projectService = new ProjectService(
                projectRepository,
                userRepository,
                ministryRepository,
                milestoneRepository,
                financialRecordRepository,
                riskAssessmentRepository,
                alertRepository,
                passwordEncoder
        );

        User officer = new User();
        officer.setId(10L);
        officer.setUsername("officer_assigned");
        officer.setRole("OFFICER");

        project = new Project();
        project.setId(100L);
        project.setName("Highway Project");
        project.setOfficer(officer);

        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(milestoneRepository.findByProjectIdOrderByReportedDateDesc(100L)).thenReturn(Collections.emptyList());
        when(financialRecordRepository.findTopByProjectIdOrderByReportedDateDescIdDesc(100L)).thenReturn(Optional.empty());
        when(riskAssessmentRepository.findTopByProjectIdOrderByAssessedAtDescIdDesc(100L)).thenReturn(Optional.empty());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetLatestRisk_AssignedOfficer_Allowed() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "officer_assigned",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OFFICER"))
                )
        );

        assertDoesNotThrow(() -> projectService.getLatestRisk(100L));
    }

    @Test
    void testGetLatestRisk_UnassignedOfficer_Throws403() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "officer_other",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OFFICER"))
                )
        );

        assertThrows(AccessDeniedException.class, () -> projectService.getLatestRisk(100L));
    }

    @Test
    void testGetLatestRisk_Admin_Allowed() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin_user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        assertDoesNotThrow(() -> projectService.getLatestRisk(100L));
    }

    @Test
    void testRefreshRisk_AssignedOfficer_Allowed() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "officer_assigned",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OFFICER"))
                )
        );

        assertDoesNotThrow(() -> projectService.refreshRisk(100L));
    }

    @Test
    void testRefreshRisk_UnassignedOfficer_Throws403() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "officer_other",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OFFICER"))
                )
        );

        assertThrows(AccessDeniedException.class, () -> projectService.refreshRisk(100L));
    }

    @Test
    void testRefreshRisk_Admin_Allowed() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin_user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        assertDoesNotThrow(() -> projectService.refreshRisk(100L));
    }
}
