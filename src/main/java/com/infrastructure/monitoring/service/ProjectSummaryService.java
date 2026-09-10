package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.ProjectSummaryDTO;
import com.infrastructure.monitoring.entity.*;
import com.infrastructure.monitoring.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ProjectSummaryService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertRepository alertRepository;

    public ProjectSummaryService(
            ProjectRepository projectRepository,
            MilestoneRepository milestoneRepository,
            FinancialRecordRepository financialRecordRepository,
            RiskAssessmentRepository riskAssessmentRepository,
            AlertRepository alertRepository) {

        this.projectRepository = projectRepository;
        this.milestoneRepository = milestoneRepository;
        this.financialRecordRepository = financialRecordRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.alertRepository = alertRepository;
    }

    public ProjectSummaryDTO getProjectSummary(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElse(null);

        if (project == null) {
            return null;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isViewerOnly = false;

        if (auth != null && auth.isAuthenticated()) {
            boolean isOfficerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isOfficerOnly) {
                String username = auth.getName();
                if (project.getOfficer() == null || !username.equalsIgnoreCase(project.getOfficer().getUsername())) {
                    throw new AccessDeniedException("You are not assigned to view this project");
                }
            }

            isViewerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_VIEWER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_OFFICER"));
        }

        List<Milestone> projectMilestones = milestoneRepository.findAll().stream()
                .filter(m -> projectId.equals(m.getProjectId()))
                .sorted(Comparator.comparing(Milestone::getReportedDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        Milestone latestProgress = projectMilestones.isEmpty() ? null : projectMilestones.get(projectMilestones.size() - 1);

        List<FinancialRecord> projectFinancials = financialRecordRepository.findAll().stream()
                .filter(f -> projectId.equals(f.getProjectId()))
                .sorted(Comparator.comparing(FinancialRecord::getReportedDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        FinancialRecord latestFinancial = projectFinancials.isEmpty() ? null : projectFinancials.get(projectFinancials.size() - 1);

        RiskAssessment latestRisk = null;
        List<Alert> activeAlerts = Collections.emptyList();

        // Viewers are strictly forbidden from receiving Risk and Alerts
        if (!isViewerOnly) {
            List<RiskAssessment> riskAssessments =
                    riskAssessmentRepository.findAll();

            latestRisk = riskAssessments.stream()
                    .filter(r -> projectId.equals(r.getProjectId()))
                    .max((a, b) -> a.getAssessedAt()
                            .compareTo(b.getAssessedAt()))
                    .orElse(null);

            activeAlerts = alertRepository.findAll().stream()
                    .filter(a -> projectId.equals(a.getProjectId()))
                    .filter(a -> Boolean.FALSE.equals(a.getResolved()))
                    .toList();
        }

        return new ProjectSummaryDTO(
                project,
                latestProgress,
                latestFinancial,
                latestRisk,
                activeAlerts,
                projectMilestones,
                projectFinancials
        );
    }
}