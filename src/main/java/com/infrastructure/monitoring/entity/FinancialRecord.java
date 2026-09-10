
package com.infrastructure.monitoring.entity;

import jakarta.persistence.*;
        import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financial_record")
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "reported_date")
    private LocalDate reportedDate;

    @Column(name = "new_expenditure", precision = 15, scale = 2)
    private BigDecimal newExpenditure;

    @Column(name = "cumulative_expenditure", precision = 15, scale = 2)
    private BigDecimal cumulativeExpenditure;

    @Column(name = "planned_expenditure", precision = 15, scale = 2)
    private BigDecimal plannedExpenditure;

    // Getters and Setters

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

    public BigDecimal getNewExpenditure() {
        return newExpenditure;
    }

    public void setNewExpenditure(BigDecimal newExpenditure) {
        this.newExpenditure = newExpenditure;
    }

    public BigDecimal getCumulativeExpenditure() {
        return cumulativeExpenditure;
    }

    public void setCumulativeExpenditure(BigDecimal cumulativeExpenditure) {
        this.cumulativeExpenditure = cumulativeExpenditure;
    }

    public BigDecimal getPlannedExpenditure() {
        return plannedExpenditure;
    }

    public void setPlannedExpenditure(BigDecimal plannedExpenditure) {
        this.plannedExpenditure = plannedExpenditure;
    }

    // Roadmap-compliant aliases
    public BigDecimal getPlannedExpenditureToDate() {
        return plannedExpenditure;
    }

    public void setPlannedExpenditureToDate(BigDecimal plannedExpenditureToDate) {
        this.plannedExpenditure = plannedExpenditureToDate;
    }
}
