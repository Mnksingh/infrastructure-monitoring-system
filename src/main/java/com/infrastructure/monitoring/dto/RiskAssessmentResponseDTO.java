package com.infrastructure.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RiskAssessmentResponseDTO {

    private Long id;
    private Long projectId;
    private LocalDateTime assessedAt;
    private BigDecimal riskProbability;
    private String riskBand;
    private String topFeatures;
    private String recommendationText;

    public RiskAssessmentResponseDTO() {
    }

    public RiskAssessmentResponseDTO(Long id, Long projectId, LocalDateTime assessedAt, BigDecimal riskProbability, String riskBand, String topFeatures, String recommendationText) {
        this.id = id;
        this.projectId = projectId;
        this.assessedAt = assessedAt;
        this.riskProbability = riskProbability;
        this.riskBand = riskBand;
        this.topFeatures = topFeatures;
        this.recommendationText = recommendationText;
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

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }

    public BigDecimal getRiskProbability() {
        return riskProbability;
    }

    public void setRiskProbability(BigDecimal riskProbability) {
        this.riskProbability = riskProbability;
    }

    public String getRiskBand() {
        return riskBand;
    }

    public void setRiskBand(String riskBand) {
        this.riskBand = riskBand;
    }

    public String getTopFeatures() {
        return topFeatures;
    }

    public void setTopFeatures(String topFeatures) {
        this.topFeatures = topFeatures;
    }

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }

    // Alias for compatibility
    public String getRecommendation() {
        return recommendationText;
    }

    public void setRecommendation(String recommendation) {
        this.recommendationText = recommendation;
    }
}
