
package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}