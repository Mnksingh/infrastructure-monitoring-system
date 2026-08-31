package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.ProjectDashboardDTO;
import com.infrastructure.monitoring.entity.*;
import com.infrastructure.monitoring.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class ProjectDashboardService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertRepository alertRepository;

    public ProjectDashboardService(
            ProjectRepository projectRepository,
            MilestoneRepository milestoneRepository,
            RiskAssessmentRepository riskAssessmentRepository,
            AlertRepository alertRepository) {

        this.projectRepository = projectRepository;
        this.milestoneRepository = milestoneRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.alertRepository = alertRepository;
    }

    public List<ProjectDashboardDTO> getAllProjectDashboardData() {

        List<Project> projects = projectRepository.findAll();
        List<Milestone> milestones = milestoneRepository.findAll();
        List<RiskAssessment> riskAssessments =
                riskAssessmentRepository.findAll();
        List<Alert> alerts = alertRepository.findAll();

        List<ProjectDashboardDTO> result = new ArrayList<>();

        for (Project project : projects) {

            // Latest milestone
            Milestone latestMilestone = milestones.stream()
                    .filter(m -> project.getId().equals(m.getProjectId()))
                    .max((a, b) -> a.getReportedDate()
                            .compareTo(b.getReportedDate()))
                    .orElse(null);

            // Latest risk assessment
            RiskAssessment latestRisk = riskAssessments.stream()
                    .filter(r -> project.getId().equals(r.getProjectId()))
                    .max((a, b) -> a.getAssessedAt()
                            .compareTo(b.getAssessedAt()))
                    .orElse(null);

            // Active alerts
            long activeAlerts = alerts.stream()
                    .filter(a -> project.getId().equals(a.getProjectId()))
                    .filter(a -> Boolean.FALSE.equals(a.getResolved()))
                    .count();

            // Progress
            var actualProgress = latestMilestone != null
                    ? latestMilestone.getActualPercent()
                    : null;

            var plannedProgress = latestMilestone != null
                    ? latestMilestone.getPlannedPercent()
                    : null;

            // Current status
            String status = "NO_DATA";

            if (latestMilestone != null
                    && actualProgress != null
                    && plannedProgress != null) {

                if (actualProgress.compareTo(plannedProgress) < 0) {
                    status = "DELAYED";
                } else {
                    status = "ON_TRACK";
                }
            }

            // ML risk
            String riskBand = latestRisk != null
                    ? latestRisk.getRiskBand()
                    : "UNKNOWN";

            var riskProbability = latestRisk != null
                    ? latestRisk.getRiskProbability()
                    : null;

            result.add(new ProjectDashboardDTO(
                    project.getId(),
                    project.getName(),
                    actualProgress,
                    plannedProgress,
                    status,
                    riskBand,
                    riskProbability,
                    activeAlerts
            ));
        }

        return result;
    }
}