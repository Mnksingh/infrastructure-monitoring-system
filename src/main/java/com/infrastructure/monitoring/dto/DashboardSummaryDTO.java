
package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;

public class DashboardSummaryDTO {

    private long totalProjects;
    private long delayedProjects;
    private long highRiskProjects;
    private long activeAlerts;
    private BigDecimal totalExpenditure;

    public DashboardSummaryDTO(
            long totalProjects,
            long delayedProjects,
            long highRiskProjects,
            long activeAlerts,
            BigDecimal totalExpenditure) {

        this.totalProjects = totalProjects;
        this.delayedProjects = delayedProjects;
        this.highRiskProjects = highRiskProjects;
        this.activeAlerts = activeAlerts;
        this.totalExpenditure = totalExpenditure;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public long getDelayedProjects() {
        return delayedProjects;
    }

    public long getHighRiskProjects() {
        return highRiskProjects;
    }

    public long getActiveAlerts() {
        return activeAlerts;
    }

    public BigDecimal getTotalExpenditure() {
        return totalExpenditure;
    }
}