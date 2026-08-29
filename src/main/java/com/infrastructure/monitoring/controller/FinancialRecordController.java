
package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.entity.FinancialRecord;
import com.infrastructure.monitoring.service.FinancialRecordService;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/financial-records")
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    public FinancialRecordController(FinancialRecordService financialRecordService) {
        this.financialRecordService = financialRecordService;
    }

    @PostMapping
    public FinancialRecord createFinancialRecord(
            @RequestBody FinancialRecord record) {

        return financialRecordService.createFinancialRecord(record);
    }

    @GetMapping
    public List<FinancialRecord> getAllFinancialRecords() {
        return financialRecordService.getAllFinancialRecords();
    }
}