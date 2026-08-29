package com.infrastructure.monitoring.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "milestone")
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "reported_date")
    private LocalDate reportedDate;

    @Column(name = "planned_percent", precision = 5, scale = 2)
    private BigDecimal plannedPercent;

    @Column(name = "actual_percent", precision = 5, scale = 2)
    private BigDecimal actualPercent;

    @Column(columnDefinition = "TEXT")
    private String remarks;

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

    public BigDecimal getPlannedPercent() {
        return plannedPercent;
    }

    public void setPlannedPercent(BigDecimal plannedPercent) {
        this.plannedPercent = plannedPercent;
    }

    public BigDecimal getActualPercent() {
        return actualPercent;
    }

    public void setActualPercent(BigDecimal actualPercent) {
        this.actualPercent = actualPercent;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}