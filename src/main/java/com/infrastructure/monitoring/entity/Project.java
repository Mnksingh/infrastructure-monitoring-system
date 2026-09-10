package com.infrastructure.monitoring.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.infrastructure.monitoring.entity.User;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "ministry_id")
    private Long ministryId;

    @Column(name = "original_cost", precision = 15, scale = 2)
    private BigDecimal originalCost;

    @Column(name = "revised_cost", precision = 15, scale = 2)
    private BigDecimal revisedCost;

    @Column(name = "original_completion")
    private LocalDate originalCompletion;

    @Column(name = "revised_completion")
    private LocalDate revisedCompletion;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "current_status")
    private String currentStatus;

    private String state;

    @Column(name = "implementing_agency")
    private String implementingAgency;

    // Assigned Officer
    @ManyToOne
    @JoinColumn(name = "officer_id")
    private User officer;


    // Getters and Setters

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

    public User getOfficer() {
        return officer;
    }

    public void setOfficer(User officer) {
        this.officer = officer;
    }

    // Roadmap-compliant aliases
    public LocalDate getOriginalCompletionDate() {
        return originalCompletion;
    }

    public void setOriginalCompletionDate(LocalDate originalCompletionDate) {
        this.originalCompletion = originalCompletionDate;
    }

    public LocalDate getRevisedCompletionDate() {
        return revisedCompletion;
    }

    public void setRevisedCompletionDate(LocalDate revisedCompletionDate) {
        this.revisedCompletion = revisedCompletionDate;
    }
}