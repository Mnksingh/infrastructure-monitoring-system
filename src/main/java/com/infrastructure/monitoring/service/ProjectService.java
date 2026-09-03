package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.CreateProjectRequest;
import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.entity.User;
import com.infrastructure.monitoring.repository.ProjectRepository;
import com.infrastructure.monitoring.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Project createProject(CreateProjectRequest request) {

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() ->
                        new RuntimeException("Officer not found"));

        if (!"OFFICER".equals(officer.getRole())) {
            throw new RuntimeException(
                    "Selected user is not an officer"
            );
        }

        Project project = new Project();

        project.setName(request.getName());
        project.setMinistryId(request.getMinistryId());
        project.setOriginalCost(request.getOriginalCost());
        project.setRevisedCost(request.getRevisedCost());
        project.setOriginalCompletion(request.getOriginalCompletion());
        project.setRevisedCompletion(request.getRevisedCompletion());
        project.setStartDate(request.getStartDate());
        project.setCurrentStatus(request.getCurrentStatus());
        project.setState(request.getState());
        project.setImplementingAgency(request.getImplementingAgency());

        // Assign officer
        project.setOfficer(officer);

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project updateProject(Long id, Project project) {

        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        // ADMIN can update any project
        if (!role.equals("ROLE_ADMIN")) {

            User assignedOfficer = existingProject.getOfficer();

            // OFFICER can update only their assigned project
            if (assignedOfficer == null ||
                    !assignedOfficer.getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You are not assigned to this project"
                );
            }
        }

        existingProject.setName(project.getName());
        existingProject.setMinistryId(project.getMinistryId());
        existingProject.setOriginalCost(project.getOriginalCost());
        existingProject.setRevisedCost(project.getRevisedCost());
        existingProject.setOriginalCompletion(
                project.getOriginalCompletion()
        );
        existingProject.setRevisedCompletion(
                project.getRevisedCompletion()
        );
        existingProject.setStartDate(project.getStartDate());
        existingProject.setCurrentStatus(project.getCurrentStatus());
        existingProject.setState(project.getState());
        existingProject.setImplementingAgency(
                project.getImplementingAgency()
        );

        return projectRepository.save(existingProject);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}