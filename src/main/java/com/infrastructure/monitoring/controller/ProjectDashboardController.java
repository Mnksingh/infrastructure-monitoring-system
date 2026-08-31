package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.ProjectDashboardDTO;
import com.infrastructure.monitoring.service.ProjectDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class ProjectDashboardController {

    private final ProjectDashboardService projectDashboardService;

    public ProjectDashboardController(
            ProjectDashboardService projectDashboardService) {
        this.projectDashboardService = projectDashboardService;
    }

    @GetMapping("/projects")
    public List<ProjectDashboardDTO> getProjectDashboardData() {
        return projectDashboardService.getAllProjectDashboardData();
    }
}