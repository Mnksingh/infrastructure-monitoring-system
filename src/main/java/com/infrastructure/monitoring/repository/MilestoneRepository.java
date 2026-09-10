package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByProjectId(Long projectId);
    List<Milestone> findByProjectIdOrderByReportedDateDesc(Long projectId);
    Optional<Milestone> findTopByProjectIdOrderByReportedDateDesc(Long projectId);
}