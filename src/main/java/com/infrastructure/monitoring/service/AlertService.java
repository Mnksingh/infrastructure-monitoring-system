package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.AlertResponseDTO;
import com.infrastructure.monitoring.entity.Alert;
import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.exception.ResourceNotFoundException;
import com.infrastructure.monitoring.repository.AlertRepository;
import com.infrastructure.monitoring.repository.ProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final ProjectRepository projectRepository;

    public AlertService(AlertRepository alertRepository, ProjectRepository projectRepository) {
        this.alertRepository = alertRepository;
        this.projectRepository = projectRepository;
    }

    // --- Legacy methods for backward compatibility ---

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Alert getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    public Alert createAlert(Alert alert) {
        if (alert.getCreatedAt() == null) {
            alert.setCreatedAt(LocalDateTime.now());
        }
        if (alert.getResolved() == null) {
            alert.setResolved(false);
        }
        return alertRepository.save(alert);
    }

    public Alert updateAlert(Long id, Alert alert) {
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alert not found with id: " + id);
        }
        alert.setId(id);
        return alertRepository.save(alert);
    }

    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
    }

    // --- Roadmap-compliant methods ---

    public List<AlertResponseDTO> getAlertResponses(Boolean resolved, String severity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentOfficer = null;
        if (auth != null && auth.isAuthenticated()) {
            boolean isOfficerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isOfficerOnly) {
                currentOfficer = auth.getName();
            }
        }

        List<Project> allProjects = projectRepository.findAll();
        Set<Long> allowedProjectIds = new HashSet<>();
        Map<Long, String> projectNames = new HashMap<>();

        for (Project project : allProjects) {
            projectNames.put(project.getId(), project.getName());
            if (currentOfficer == null) {
                allowedProjectIds.add(project.getId());
            } else if (project.getOfficer() != null && currentOfficer.equalsIgnoreCase(project.getOfficer().getUsername())) {
                allowedProjectIds.add(project.getId());
            }
        }

        List<Alert> alerts;
        if (resolved != null) {
            alerts = alertRepository.findByResolvedOrderByCreatedAtDesc(resolved);
        } else {
            alerts = alertRepository.findAllByOrderByCreatedAtDesc();
        }

        List<AlertResponseDTO> dtos = new ArrayList<>();
        for (Alert alert : alerts) {
            if (currentOfficer != null && !allowedProjectIds.contains(alert.getProjectId())) {
                continue;
            }
            if (severity != null && !severity.isBlank() && !severity.equalsIgnoreCase(alert.getSeverity())) {
                continue;
            }

            dtos.add(new AlertResponseDTO(
                    alert.getId(),
                    alert.getProjectId(),
                    projectNames.getOrDefault(alert.getProjectId(), "Project #" + alert.getProjectId()),
                    alert.getRiskAssessmentId(),
                    alert.getSeverity(),
                    alert.getMessage(),
                    alert.getCreatedAt(),
                    alert.getResolved(),
                    alert.getResolvedAt()
            ));
        }

        return dtos;
    }

    @Transactional
    public AlertResponseDTO resolveAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isOfficerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isOfficerOnly) {
                String username = auth.getName();
                Project project = alert.getProjectId() != null ? projectRepository.findById(alert.getProjectId()).orElse(null) : null;
                if (project == null || project.getOfficer() == null || !username.equalsIgnoreCase(project.getOfficer().getUsername())) {
                    throw new AccessDeniedException("You are not assigned to resolve alerts for this project");
                }
            }
        }

        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        Alert saved = alertRepository.save(alert);

        String projectName = alert.getProjectId() != null
                ? projectRepository.findById(alert.getProjectId()).map(Project::getName).orElse("Project #" + alert.getProjectId())
                : "Unknown Project";

        return new AlertResponseDTO(
                saved.getId(),
                saved.getProjectId(),
                projectName,
                saved.getRiskAssessmentId(),
                saved.getSeverity(),
                saved.getMessage(),
                saved.getCreatedAt(),
                saved.getResolved(),
                saved.getResolvedAt()
        );
    }
}