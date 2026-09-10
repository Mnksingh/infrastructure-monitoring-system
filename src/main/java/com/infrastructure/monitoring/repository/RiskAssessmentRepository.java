package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    Optional<RiskAssessment> findTopByProjectIdOrderByAssessedAtDescIdDesc(Long projectId);
    List<RiskAssessment> findByProjectIdOrderByAssessedAtDesc(Long projectId);
}