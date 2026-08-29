package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}