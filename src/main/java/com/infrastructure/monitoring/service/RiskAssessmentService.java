package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.entity.RiskAssessment;
import com.infrastructure.monitoring.repository.RiskAssessmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;

    public RiskAssessmentService(RiskAssessmentRepository riskAssessmentRepository) {
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    public List<RiskAssessment> getAllRiskAssessments() {
        return riskAssessmentRepository.findAll();
    }

    public RiskAssessment getRiskAssessmentById(Long id) {
        return riskAssessmentRepository.findById(id).orElse(null);
    }

    public RiskAssessment createRiskAssessment(RiskAssessment riskAssessment) {
        return riskAssessmentRepository.save(riskAssessment);
    }

    public RiskAssessment updateRiskAssessment(Long id, RiskAssessment riskAssessment) {
        riskAssessment.setId(id);
        return riskAssessmentRepository.save(riskAssessment);
    }

    public void deleteRiskAssessment(Long id) {
        riskAssessmentRepository.deleteById(id);
    }
}