package com.infrastructure.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MilestoneRequestDTO {

    @NotNull(message = "Reported date is required")
    private LocalDate reportedDate;

    @NotNull(message = "Planned physical progress percent is required")
    @DecimalMin(value = "0.0", message = "Planned percent must be >= 0")
    @DecimalMax(value = "100.0", message = "Planned percent must be <= 100")
    @JsonAlias({"plannedPercent"})
    private BigDecimal plannedPhysicalProgressPct;

    @NotNull(message = "Actual physical progress percent is required")
    @DecimalMin(value = "0.0", message = "Actual percent must be >= 0")
    @DecimalMax(value = "100.0", message = "Actual percent must be <= 100")
    @JsonAlias({"actualPercent"})
    private BigDecimal actualPhysicalProgressPct;

    private String remarks;

    public MilestoneRequestDTO() {
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

    // Compatibility alias
    public BigDecimal getPlannedPercent() {
        return plannedPhysicalProgressPct;
    }

    public void setPlannedPercent(BigDecimal plannedPercent) {
        this.plannedPhysicalProgressPct = plannedPercent;
    }

    public BigDecimal getActualPhysicalProgressPct() {
        return actualPhysicalProgressPct;
    }

    public void setActualPhysicalProgressPct(BigDecimal actualPhysicalProgressPct) {
        this.actualPhysicalProgressPct = actualPhysicalProgressPct;
    }

    // Compatibility alias
    public BigDecimal getActualPercent() {
        return actualPhysicalProgressPct;
    }

    public void setActualPercent(BigDecimal actualPercent) {
        this.actualPhysicalProgressPct = actualPercent;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
