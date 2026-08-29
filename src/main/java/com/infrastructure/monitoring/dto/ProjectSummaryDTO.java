
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

    public ProjectSummaryDTO(
            Project project,
            Milestone latestProgress,
            FinancialRecord latestFinancial,
            RiskAssessment latestRisk,
            List<Alert> activeAlerts) {

        this.project = project;
        this.latestProgress = latestProgress;
        this.latestFinancial = latestFinancial;
        this.latestRisk = latestRisk;
        this.activeAlerts = activeAlerts;
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
}