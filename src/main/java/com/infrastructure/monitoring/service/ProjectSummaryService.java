
package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.ProjectSummaryDTO;
import com.infrastructure.monitoring.entity.*;
        import com.infrastructure.monitoring.repository.*;

        import org.springframework.stereotype.Service;

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

        List<Milestone> milestones = milestoneRepository.findAll();
        Milestone latestProgress = milestones.stream()
                .filter(m -> projectId.equals(m.getProjectId()))
                .max((a, b) -> a.getReportedDate()
                        .compareTo(b.getReportedDate()))
                .orElse(null);

        List<FinancialRecord> financialRecords =
                financialRecordRepository.findAll();

        FinancialRecord latestFinancial = financialRecords.stream()
                .filter(f -> projectId.equals(f.getProjectId()))
                .max((a, b) -> a.getReportedDate()
                        .compareTo(b.getReportedDate()))
                .orElse(null);

        List<RiskAssessment> riskAssessments =
                riskAssessmentRepository.findAll();

        RiskAssessment latestRisk = riskAssessments.stream()
                .filter(r -> projectId.equals(r.getProjectId()))
                .max((a, b) -> a.getAssessedAt()
                        .compareTo(b.getAssessedAt()))
                .orElse(null);

        List<Alert> activeAlerts = alertRepository.findAll().stream()
                .filter(a -> projectId.equals(a.getProjectId()))
                .filter(a -> Boolean.FALSE.equals(a.getResolved()))
                .toList();

        return new ProjectSummaryDTO(
                project,
                latestProgress,
                latestFinancial,
                latestRisk,
                activeAlerts
        );
    }
}