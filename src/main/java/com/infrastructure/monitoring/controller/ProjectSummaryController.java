
package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.ProjectSummaryDTO;
import com.infrastructure.monitoring.service.ProjectSummaryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectSummaryController {

    private final ProjectSummaryService projectSummaryService;

    public ProjectSummaryController(ProjectSummaryService projectSummaryService) {
        this.projectSummaryService = projectSummaryService;
    }

    @GetMapping("/{id}/summary")
    public ProjectSummaryDTO getProjectSummary(@PathVariable Long id) {
        return projectSummaryService.getProjectSummary(id);
    }
}
