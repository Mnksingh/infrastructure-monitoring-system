
package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(Project project) {
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
                .orElseThrow(() -> new RuntimeException("Project not found"));

        existingProject.setName(project.getName());
        existingProject.setMinistryId(project.getMinistryId());
        existingProject.setOriginalCost(project.getOriginalCost());
        existingProject.setRevisedCost(project.getRevisedCost());
        existingProject.setOriginalCompletion(project.getOriginalCompletion());
        existingProject.setRevisedCompletion(project.getRevisedCompletion());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setCurrentStatus(project.getCurrentStatus());
        existingProject.setState(project.getState());
        existingProject.setImplementingAgency(project.getImplementingAgency());

        return projectRepository.save(existingProject);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}