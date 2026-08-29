package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
}