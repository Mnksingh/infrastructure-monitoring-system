
package com.infrastructure.monitoring.repository;

import com.infrastructure.monitoring.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Optional<FinancialRecord> findTopByProjectIdOrderByReportedDateDescIdDesc(Long projectId);
    List<FinancialRecord> findByProjectId(Long projectId);
    List<FinancialRecord> findByProjectIdOrderByReportedDateDesc(Long projectId);

}