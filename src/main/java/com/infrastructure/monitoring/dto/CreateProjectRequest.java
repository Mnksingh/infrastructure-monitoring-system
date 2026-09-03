package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateProjectRequest {

    private String name;
    private Long ministryId;
    private BigDecimal originalCost;
    private BigDecimal revisedCost;
    private LocalDate originalCompletion;
    private LocalDate revisedCompletion;
    private LocalDate startDate;
    private String currentStatus;
    private String state;
    private String implementingAgency;

    private Long officerId;


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

    public LocalDate getOriginalCompletion() {
        return originalCompletion;
    }

    public void setOriginalCompletion(LocalDate originalCompletion) {
        this.originalCompletion = originalCompletion;
    }

    public LocalDate getRevisedCompletion() {
        return revisedCompletion;
    }

    public void setRevisedCompletion(LocalDate revisedCompletion) {
        this.revisedCompletion = revisedCompletion;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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