package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectResponseDTO {

    private Long id;
    private String name;
    private Long ministryId;
    private String ministryName;
    private String sector;
    private BigDecimal originalCost;
    private BigDecimal revisedCost;
    private LocalDate startDate;
    private LocalDate originalCompletionDate;
    private LocalDate revisedCompletionDate;
    private String currentStatus;
    private String state;
    private String implementingAgency;
    private Long officerId;
    private String officerUsername;

    private BigDecimal latestProgressPct;
    private String latestRiskBand;
    private BigDecimal latestRiskProbability;
    private long activeAlertsCount;

    public ProjectResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMinistryName() {
        return ministryName;
    }

    public void setMinistryName(String ministryName) {
        this.ministryName = ministryName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
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

    public LocalDate getRevisedCompletionDate() {
        return revisedCompletionDate;
    }

    public void setRevisedCompletionDate(LocalDate revisedCompletionDate) {
        this.revisedCompletionDate = revisedCompletionDate;
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

    public String getOfficerUsername() {
        return officerUsername;
    }

    public void setOfficerUsername(String officerUsername) {
        this.officerUsername = officerUsername;
    }

    public BigDecimal getLatestProgressPct() {
        return latestProgressPct;
    }

    public void setLatestProgressPct(BigDecimal latestProgressPct) {
        this.latestProgressPct = latestProgressPct;
    }

    public String getLatestRiskBand() {
        return latestRiskBand;
    }

    public void setLatestRiskBand(String latestRiskBand) {
        this.latestRiskBand = latestRiskBand;
    }

    public BigDecimal getLatestRiskProbability() {
        return latestRiskProbability;
    }

    public void setLatestRiskProbability(BigDecimal latestRiskProbability) {
        this.latestRiskProbability = latestRiskProbability;
    }

    public long getActiveAlertsCount() {
        return activeAlertsCount;
    }

    public void setActiveAlertsCount(long activeAlertsCount) {
        this.activeAlertsCount = activeAlertsCount;
    }
}
