package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByResolved(Boolean resolved);
    List<Alert> findByProjectId(Long projectId);
    List<Alert> findByProjectIdAndResolved(Long projectId, Boolean resolved);
    List<Alert> findByResolvedOrderByCreatedAtDesc(Boolean resolved);
    List<Alert> findAllByOrderByCreatedAtDesc();
}