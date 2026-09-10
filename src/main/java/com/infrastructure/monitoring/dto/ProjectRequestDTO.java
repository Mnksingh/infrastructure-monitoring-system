package com.infrastructure.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectRequestDTO {

    @NotBlank(message = "Project name is required")
    private String name;

    @NotNull(message = "Ministry ID is required")
    private Long ministryId;

    @NotNull(message = "Original cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Original cost must be greater than 0")
    private BigDecimal originalCost;

    private BigDecimal revisedCost;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Original completion date is required")
    @JsonAlias({"originalCompletion"})
    private LocalDate originalCompletionDate;

    @JsonAlias({"revisedCompletion"})
    private LocalDate revisedCompletionDate;

    private String currentStatus;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Implementing agency is required")
    private String implementingAgency;

    private Long officerId;
    private String officerEmail;

    public ProjectRequestDTO() {
    }

    public String getOfficerEmail() {
        return officerEmail;
    }

    public void setOfficerEmail(String officerEmail) {
        this.officerEmail = officerEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMinistryId() {
        return ministryId;
    }

    public void setMinistryId(Long ministryId) {
        this.ministryId = ministryId;
    }

    public BigDecimal getOriginalCost() {
        return originalCost;
    }

    public void setOriginalCost(BigDecimal originalCost) {
        this.originalCost = originalCost;
    }

    public BigDecimal getRevisedCost() {
        return revisedCost;
    }

    public void setRevisedCost(BigDecimal revisedCost) {
        this.revisedCost = revisedCost;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getOriginalCompletionDate() {
        return originalCompletionDate;
    }

    public void setOriginalCompletionDate(LocalDate originalCompletionDate) {
        this.originalCompletionDate = originalCompletionDate;
    }

    // Alias for backwards compatibility
    public LocalDate getOriginalCompletion() {
        return originalCompletionDate;
    }

    public void setOriginalCompletion(LocalDate originalCompletion) {
        this.originalCompletionDate = originalCompletion;
    }

    public LocalDate getRevisedCompletionDate() {
        return revisedCompletionDate;
    }

    public void setRevisedCompletionDate(LocalDate revisedCompletionDate) {
        this.revisedCompletionDate = revisedCompletionDate;
    }

    // Alias for backwards compatibility
    public LocalDate getRevisedCompletion() {
        return revisedCompletionDate;
    }

    public void setRevisedCompletion(LocalDate revisedCompletion) {
        this.revisedCompletionDate = revisedCompletion;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImplementingAgency() {
        return implementingAgency;
    }

    public void setImplementingAgency(String implementingAgency) {
        this.implementingAgency = implementingAgency;
    }

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }
}
