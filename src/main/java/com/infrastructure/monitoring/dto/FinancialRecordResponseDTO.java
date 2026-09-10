package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialRecordResponseDTO {

    private Long id;
    private Long projectId;
    private LocalDate reportedDate;
    private BigDecimal cumulativeExpenditure;
    private BigDecimal plannedExpenditureToDate;
    private BigDecimal newExpenditure;

    public FinancialRecordResponseDTO() {
    }

    public FinancialRecordResponseDTO(Long id, Long projectId, LocalDate reportedDate, BigDecimal cumulativeExpenditure, BigDecimal plannedExpenditureToDate, BigDecimal newExpenditure) {
        this.id = id;
        this.projectId = projectId;
        this.reportedDate = reportedDate;
        this.cumulativeExpenditure = cumulativeExpenditure;
        this.plannedExpenditureToDate = plannedExpenditureToDate;
        this.newExpenditure = newExpenditure;
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

    public BigDecimal getCumulativeExpenditure() {
        return cumulativeExpenditure;
    }

    public void setCumulativeExpenditure(BigDecimal cumulativeExpenditure) {
        this.cumulativeExpenditure = cumulativeExpenditure;
    }

    public BigDecimal getPlannedExpenditureToDate() {
        return plannedExpenditureToDate;
    }

    public void setPlannedExpenditureToDate(BigDecimal plannedExpenditureToDate) {
        this.plannedExpenditureToDate = plannedExpenditureToDate;
    }

    public BigDecimal getNewExpenditure() {
        return newExpenditure;
    }

    public void setNewExpenditure(BigDecimal newExpenditure) {
        this.newExpenditure = newExpenditure;
    }
}
