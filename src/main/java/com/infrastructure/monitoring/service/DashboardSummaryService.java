package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.DashboardSummaryDTO;
import com.infrastructure.monitoring.entity.*;
import com.infrastructure.monitoring.repository.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardSummaryService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertRepository alertRepository;

    public DashboardSummaryService(
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

    public DashboardSummaryDTO getDashboardSummary() {

        // 1. Total projects
        long totalProjects = projectRepository.count();

        // 2. Delayed projects
        // A project is delayed if latest actual progress
        // is less than latest planned progress.
        List<Milestone> milestones = milestoneRepository.findAll();

        long delayedProjects = projectRepository.findAll().stream()
                .filter(project -> {

                    Milestone latestMilestone = milestones.stream()
                            .filter(m -> project.getId().equals(m.getProjectId()))
                            .max((a, b) -> a.getReportedDate()
                                    .compareTo(b.getReportedDate()))
                            .orElse(null);

                    return latestMilestone != null
                            && latestMilestone.getActualPercent() != null
                            && latestMilestone.getPlannedPercent() != null
                            && latestMilestone.getActualPercent()
                            .compareTo(latestMilestone.getPlannedPercent()) < 0;
                })
                .count();

        // 3. High-risk projects
        // High risk means latest ML risk assessment is HIGH.
        List<RiskAssessment> riskAssessments =
                riskAssessmentRepository.findAll();

        long highRiskProjects = projectRepository.findAll().stream()
                .filter(project -> {

                    RiskAssessment latestRisk = riskAssessments.stream()
                            .filter(r -> project.getId().equals(r.getProjectId()))
                            .max((a, b) -> a.getAssessedAt()
                                    .compareTo(b.getAssessedAt()))
                            .orElse(null);

                    return latestRisk != null
                            && "HIGH".equalsIgnoreCase(latestRisk.getRiskBand());
                })
                .count();

        // 4. Active alerts
        long activeAlerts = alertRepository.findAll().stream()
                .filter(alert -> Boolean.FALSE.equals(alert.getResolved()))
                .count();

        // 5. Total expenditure
        // Take the latest cumulative expenditure of each project.
        List<FinancialRecord> financialRecords =
                financialRecordRepository.findAll();

        BigDecimal totalExpenditure = projectRepository.findAll().stream()
                .map(project -> financialRecords.stream()
                        .filter(f -> project.getId().equals(f.getProjectId()))
                        .max((a, b) -> a.getReportedDate()
                                .compareTo(b.getReportedDate()))
                        .map(FinancialRecord::getCumulativeExpenditure)
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardSummaryDTO(
                totalProjects,
                delayedProjects,
                highRiskProjects,
                activeAlerts,
                totalExpenditure
        );
    }
}