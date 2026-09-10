package com.infrastructure.monitoring.dto;

import java.time.LocalDateTime;

public class AlertResponseDTO {

    private Long id;
    private Long projectId;
    private String projectName;
    private Long riskAssessmentId;
    private String severity;
    private String message;
    private LocalDateTime createdAt;
    private Boolean resolved;
    private LocalDateTime resolvedAt;

    public AlertResponseDTO() {
    }

    public AlertResponseDTO(Long id, Long projectId, String projectName, Long riskAssessmentId, String severity, String message, LocalDateTime createdAt, Boolean resolved, LocalDateTime resolvedAt) {
        this.id = id;
        this.projectId = projectId;
        this.projectName = projectName;
        this.riskAssessmentId = riskAssessmentId;
        this.severity = severity;
        this.message = message;
        this.createdAt = createdAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getRiskAssessmentId() {
        return riskAssessmentId;
    }

    public void setRiskAssessmentId(Long riskAssessmentId) {
        this.riskAssessmentId = riskAssessmentId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
