package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Ministry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinistryRepository extends JpaRepository<Ministry, Long> {
}