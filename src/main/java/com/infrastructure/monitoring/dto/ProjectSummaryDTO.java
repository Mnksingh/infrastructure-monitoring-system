package com.infrastructure.monitoring.dto;

import com.infrastructure.monitoring.entity.Alert;
import com.infrastructure.monitoring.entity.FinancialRecord;
import com.infrastructure.monitoring.entity.Milestone;
import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.entity.RiskAssessment;

import java.util.List;

public class ProjectSummaryDTO {

    private Project project;
    private Milestone latestProgress;
    private FinancialRecord latestFinancial;
    private RiskAssessment latestRisk;
    private List<Alert> activeAlerts;
    private List<Milestone> milestones;
    private List<FinancialRecord> financialRecords;

    public ProjectSummaryDTO(
            Project project,
            Milestone latestProgress,
            FinancialRecord latestFinancial,
            RiskAssessment latestRisk,
            List<Alert> activeAlerts,
            List<Milestone> milestones,
            List<FinancialRecord> financialRecords) {

        this.project = project;
        this.latestProgress = latestProgress;
        this.latestFinancial = latestFinancial;
        this.latestRisk = latestRisk;
        this.activeAlerts = activeAlerts;
        this.milestones = milestones;
        this.financialRecords = financialRecords;
    }

    public Project getProject() {
        return project;
    }

    public Milestone getLatestProgress() {
        return latestProgress;
    }

    public FinancialRecord getLatestFinancial() {
        return latestFinancial;
    }

    public RiskAssessment getLatestRisk() {
        return latestRisk;
    }

    public List<Alert> getActiveAlerts() {
        return activeAlerts;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public List<FinancialRecord> getFinancialRecords() {
        return financialRecords;
    }
}