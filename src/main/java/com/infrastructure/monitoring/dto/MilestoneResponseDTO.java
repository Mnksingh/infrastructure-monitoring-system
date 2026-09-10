package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MilestoneResponseDTO {

    private Long id;
    private Long projectId;
    private LocalDate reportedDate;
    private BigDecimal plannedPhysicalProgressPct;
    private BigDecimal actualPhysicalProgressPct;
    private String remarks;

    public MilestoneResponseDTO() {
    }

    public MilestoneResponseDTO(Long id, Long projectId, LocalDate reportedDate, BigDecimal plannedPhysicalProgressPct, BigDecimal actualPhysicalProgressPct, String remarks) {
        this.id = id;
        this.projectId = projectId;
        this.reportedDate = reportedDate;
        this.plannedPhysicalProgressPct = plannedPhysicalProgressPct;
        this.actualPhysicalProgressPct = actualPhysicalProgressPct;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public LocalDate getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDate reportedDate) {
        this.reportedDate = reportedDate;
    }

    public BigDecimal getPlannedPhysicalProgressPct() {
        return plannedPhysicalProgressPct;
    }

    public void setPlannedPhysicalProgressPct(BigDecimal plannedPhysicalProgressPct) {
        this.plannedPhysicalProgressPct = plannedPhysicalProgressPct;
    }

    public BigDecimal getActualPhysicalProgressPct() {
        return actualPhysicalProgressPct;
    }

    public void setActualPhysicalProgressPct(BigDecimal actualPhysicalProgressPct) {
        this.actualPhysicalProgressPct = actualPhysicalProgressPct;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
