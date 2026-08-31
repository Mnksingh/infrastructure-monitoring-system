package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;

public class ProjectDashboardDTO {

    private Long projectId;
    private String projectName;
    private BigDecimal actualProgress;
    private BigDecimal plannedProgress;
    private String status;
    private String riskBand;
    private BigDecimal riskProbability;
    private long activeAlerts;

    public ProjectDashboardDTO(
            Long projectId,
            String projectName,
            BigDecimal actualProgress,
            BigDecimal plannedProgress,
            String status,
            String riskBand,
            BigDecimal riskProbability,
            long activeAlerts) {

        this.projectId = projectId;
        this.projectName = projectName;
        this.actualProgress = actualProgress;
        this.plannedProgress = plannedProgress;
        this.status = status;
        this.riskBand = riskBand;
        this.riskProbability = riskProbability;
        this.activeAlerts = activeAlerts;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public BigDecimal getActualProgress() {
        return actualProgress;
    }

    public BigDecimal getPlannedProgress() {
        return plannedProgress;
    }

    public String getStatus() {
        return status;
    }

    public String getRiskBand() {
        return riskBand;
    }

    public BigDecimal getRiskProbability() {
        return riskProbability;
    }

    public long getActiveAlerts() {
        return activeAlerts;
    }
}