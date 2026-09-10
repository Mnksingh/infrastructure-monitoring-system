
package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByMinistryId(Long ministryId);
    List<Project> findByCurrentStatus(String currentStatus);
    long countByImplementingAgency(String implementingAgency);
}