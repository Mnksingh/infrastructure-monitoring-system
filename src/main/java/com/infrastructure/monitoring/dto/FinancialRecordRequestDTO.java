package com.infrastructure.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialRecordRequestDTO {

    @NotNull(message = "Reported date is required")
    private LocalDate reportedDate;

    @NotNull(message = "Cumulative expenditure is required")
    @DecimalMin(value = "0.0", message = "Cumulative expenditure must be >= 0")
    private BigDecimal cumulativeExpenditure;

    @NotNull(message = "Planned expenditure to date is required")
    @DecimalMin(value = "0.0", message = "Planned expenditure to date must be >= 0")
    @JsonAlias({"plannedExpenditure"})
    private BigDecimal plannedExpenditureToDate;

    private BigDecimal newExpenditure;

    public FinancialRecordRequestDTO() {
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

    // Compatibility alias
    public BigDecimal getPlannedExpenditure() {
        return plannedExpenditureToDate;
    }

    public void setPlannedExpenditure(BigDecimal plannedExpenditure) {
        this.plannedExpenditureToDate = plannedExpenditure;
    }

    public BigDecimal getNewExpenditure() {
        return newExpenditure;
    }

    public void setNewExpenditure(BigDecimal newExpenditure) {
        this.newExpenditure = newExpenditure;
    }
}
