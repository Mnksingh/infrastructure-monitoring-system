package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.DashboardSummaryDTO;
import com.infrastructure.monitoring.service.DashboardSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardSummaryController {

    private final DashboardSummaryService dashboardSummaryService;

    public DashboardSummaryController(
            DashboardSummaryService dashboardSummaryService) {
        this.dashboardSummaryService = dashboardSummaryService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDTO getDashboardSummary() {
        return dashboardSummaryService.getDashboardSummary();
    }
}