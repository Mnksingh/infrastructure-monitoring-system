package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}