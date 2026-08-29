package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.entity.FinancialRecord;
import com.infrastructure.monitoring.repository.FinancialRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;

    public FinancialRecordService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    public FinancialRecord createFinancialRecord(FinancialRecord record) {

        BigDecimal previousCumulative = financialRecordRepository
                .findTopByProjectIdOrderByReportedDateDescIdDesc(record.getProjectId())
                .map(FinancialRecord::getCumulativeExpenditure)
                .orElse(BigDecimal.ZERO);

        BigDecimal newCumulative = previousCumulative
                .add(record.getNewExpenditure());

        record.setCumulativeExpenditure(newCumulative);

        return financialRecordRepository.save(record);
    }

    public List<FinancialRecord> getAllFinancialRecords() {
        return financialRecordRepository.findAll();
    }
}
