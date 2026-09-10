package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.*;
import com.infrastructure.monitoring.entity.*;
import com.infrastructure.monitoring.exception.BadRequestException;
import com.infrastructure.monitoring.exception.ResourceNotFoundException;
import com.infrastructure.monitoring.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MinistryRepository ministryRepository;
    private final MilestoneRepository milestoneRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertRepository alertRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            MinistryRepository ministryRepository,
            MilestoneRepository milestoneRepository,
            FinancialRecordRepository financialRecordRepository,
            RiskAssessmentRepository riskAssessmentRepository,
            AlertRepository alertRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.ministryRepository = ministryRepository;
        this.milestoneRepository = milestoneRepository;
        this.financialRecordRepository = financialRecordRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.alertRepository = alertRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Legacy / Entity methods for backward compatibility ---

    public Project createProject(CreateProjectRequest request) {
        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + request.getOfficerId()));

        if (!"OFFICER".equals(officer.getRole())) {
            throw new BadRequestException("Selected user is not an officer");
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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        validateOfficerAssignment(existingProject);

        existingProject.setName(project.getName());
        if (project.getMinistryId() != null) {
            existingProject.setMinistryId(project.getMinistryId());
        }
        existingProject.setOriginalCost(project.getOriginalCost());
        existingProject.setRevisedCost(project.getRevisedCost());
        existingProject.setOriginalCompletion(project.getOriginalCompletion());
        existingProject.setRevisedCompletion(project.getRevisedCompletion());
        existingProject.setStartDate(project.getStartDate());
        if (project.getCurrentStatus() != null) {
            existingProject.setCurrentStatus(project.getCurrentStatus());
        }
        existingProject.setState(project.getState());
        existingProject.setImplementingAgency(project.getImplementingAgency());

        return projectRepository.save(existingProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        alertRepository.deleteAll(alertRepository.findByProjectId(id));
        milestoneRepository.deleteAll(milestoneRepository.findByProjectId(id));
        financialRecordRepository.deleteAll(financialRecordRepository.findByProjectId(id));
        riskAssessmentRepository.deleteAll(riskAssessmentRepository.findByProjectIdOrderByAssessedAtDesc(id));
        projectRepository.deleteById(id);
    }

    // --- DTO & Roadmap-compliant methods ---

    public List<ProjectResponseDTO> getAllProjectResponses(String riskFilter, String sectorFilter) {
        List<Project> projects = projectRepository.findAll();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isOfficerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isOfficerOnly) {
                String username = auth.getName();
                projects = projects.stream()
                        .filter(p -> p.getOfficer() != null && username.equalsIgnoreCase(p.getOfficer().getUsername()))
                        .toList();
            }
        }

        Map<Long, Ministry> ministries = ministryRepository.findAll().stream()
                .collect(Collectors.toMap(Ministry::getId, m -> m, (m1, m2) -> m1));

        List<ProjectResponseDTO> responses = new ArrayList<>();

        for (Project project : projects) {
            ProjectResponseDTO dto = toProjectResponseDTO(project, ministries.get(project.getMinistryId()));

            boolean matchesRisk = (riskFilter == null || riskFilter.isBlank())
                    || (dto.getLatestRiskBand() != null && dto.getLatestRiskBand().equalsIgnoreCase(riskFilter.trim()));

            boolean matchesSector = (sectorFilter == null || sectorFilter.isBlank())
                    || (dto.getSector() != null && dto.getSector().equalsIgnoreCase(sectorFilter.trim()))
                    || (dto.getMinistryName() != null && dto.getMinistryName().equalsIgnoreCase(sectorFilter.trim()));

            if (matchesRisk && matchesSector) {
                responses.add(dto);
            }
        }

        return responses;
    }

    public ProjectResponseDTO getProjectResponseById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isOfficerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isOfficerOnly) {
                String username = auth.getName();
                if (project.getOfficer() == null || !username.equalsIgnoreCase(project.getOfficer().getUsername())) {
                    throw new AccessDeniedException("You are not assigned to view this project");
                }
            }
        }

        Ministry ministry = project.getMinistryId() != null
                ? ministryRepository.findById(project.getMinistryId()).orElse(null)
                : null;

        return toProjectResponseDTO(project, ministry);
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO request) {
        User officer = null;
        if (request.getOfficerId() != null) {
            officer = userRepository.findById(request.getOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + request.getOfficerId()));

            if (!"OFFICER".equalsIgnoreCase(officer.getRole()) && !"ADMIN".equalsIgnoreCase(officer.getRole())) {
                throw new BadRequestException("Selected user is not an officer or admin");
            }
        } else if (request.getOfficerEmail() != null && !request.getOfficerEmail().trim().isEmpty()) {
            String officerIdentifier = request.getOfficerEmail().trim();
            officer = userRepository.findByUsername(officerIdentifier).orElseGet(() -> {
                User newOfficer = new User();
                newOfficer.setUsername(officerIdentifier);
                newOfficer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                newOfficer.setRole("OFFICER");
                return userRepository.save(newOfficer);
            });
        }

        if (request.getMinistryId() != null && !ministryRepository.existsById(request.getMinistryId())) {
            throw new ResourceNotFoundException("Ministry not found with id: " + request.getMinistryId());
        }

        Project project = new Project();
        mapRequestToProject(request, project);
        if (officer != null) {
            project.setOfficer(officer);
        }

        Project saved = projectRepository.save(project);

        Ministry ministry = saved.getMinistryId() != null
                ? ministryRepository.findById(saved.getMinistryId()).orElse(null)
                : null;

        return toProjectResponseDTO(saved, ministry);
    }

    @Transactional
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO request) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        validateOfficerAssignment(existingProject);

        if (request.getMinistryId() != null && !ministryRepository.existsById(request.getMinistryId())) {
            throw new ResourceNotFoundException("Ministry not found with id: " + request.getMinistryId());
        }

        if (request.getOfficerId() != null) {
            User officer = userRepository.findById(request.getOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + request.getOfficerId()));
            existingProject.setOfficer(officer);
        } else if (request.getOfficerEmail() != null && !request.getOfficerEmail().trim().isEmpty()) {
            String officerIdentifier = request.getOfficerEmail().trim();
            User officer = userRepository.findByUsername(officerIdentifier).orElseGet(() -> {
                User newOfficer = new User();
                newOfficer.setUsername(officerIdentifier);
                newOfficer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                newOfficer.setRole("OFFICER");
                return userRepository.save(newOfficer);
            });
            existingProject.setOfficer(officer);
        }

        mapRequestToProject(request, existingProject);
        Project saved = projectRepository.save(existingProject);

        Ministry ministry = saved.getMinistryId() != null
                ? ministryRepository.findById(saved.getMinistryId()).orElse(null)
                : null;

        return toProjectResponseDTO(saved, ministry);
    }

    // --- Sub-resources (Milestones, Financials, Risk) ---

    @Transactional
    public MilestoneResponseDTO addMilestone(Long projectId, MilestoneRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        validateOfficerAssignment(project);

        Milestone milestone = new Milestone();
        milestone.setProjectId(projectId);
        milestone.setReportedDate(dto.getReportedDate());
        milestone.setPlannedPhysicalProgressPct(dto.getPlannedPhysicalProgressPct());
        milestone.setActualPhysicalProgressPct(dto.getActualPhysicalProgressPct());
        milestone.setRemarks(dto.getRemarks());

        Milestone saved = milestoneRepository.save(milestone);

        // Update project status if actual progress < planned progress
        if (dto.getActualPhysicalProgressPct().compareTo(dto.getPlannedPhysicalProgressPct()) < 0) {
            project.setCurrentStatus("DELAYED");
        } else if (dto.getActualPhysicalProgressPct().compareTo(BigDecimal.valueOf(100)) >= 0) {
            project.setCurrentStatus("COMPLETED");
        } else {
            project.setCurrentStatus("ON_TRACK");
        }
        projectRepository.save(project);

        // Auto-refresh risk rule evaluation
        refreshRiskInternal(project, saved, null);

        return new MilestoneResponseDTO(
                saved.getId(),
                saved.getProjectId(),
                saved.getReportedDate(),
                saved.getPlannedPhysicalProgressPct(),
                saved.getActualPhysicalProgressPct(),
                saved.getRemarks()
        );
    }

    @Transactional
    public FinancialRecordResponseDTO addFinancialRecord(Long projectId, FinancialRecordRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        validateOfficerAssignment(project);

        FinancialRecord record = new FinancialRecord();
        record.setProjectId(projectId);
        record.setReportedDate(dto.getReportedDate());
        record.setCumulativeExpenditure(dto.getCumulativeExpenditure());
        record.setPlannedExpenditureToDate(dto.getPlannedExpenditureToDate());
        record.setNewExpenditure(dto.getNewExpenditure() != null ? dto.getNewExpenditure() : BigDecimal.ZERO);

        FinancialRecord saved = financialRecordRepository.save(record);

        // Auto-refresh risk rule evaluation
        refreshRiskInternal(project, null, saved);

        return new FinancialRecordResponseDTO(
                saved.getId(),
                saved.getProjectId(),
                saved.getReportedDate(),
                saved.getCumulativeExpenditure(),
                saved.getPlannedExpenditureToDate(),
                saved.getNewExpenditure()
        );
    }

    public RiskAssessmentResponseDTO getLatestRisk(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        validateOfficerAssignment(project);

        RiskAssessment risk = riskAssessmentRepository
                .findTopByProjectIdOrderByAssessedAtDescIdDesc(projectId)
                .orElse(null);

        if (risk == null) {
            // Trigger initial evaluation
            return refreshRisk(projectId);
        }

        return toRiskAssessmentResponseDTO(risk);
    }

    @Transactional
    public RiskAssessmentResponseDTO refreshRisk(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        validateOfficerAssignment(project);

        Milestone latestMilestone = milestoneRepository.findTopByProjectIdOrderByReportedDateDesc(projectId).orElse(null);
        if (latestMilestone == null) {
            latestMilestone = milestoneRepository.findByProjectIdOrderByReportedDateDesc(projectId).stream().findFirst().orElse(null);
        }

        FinancialRecord latestFinancial = financialRecordRepository
                .findTopByProjectIdOrderByReportedDateDescIdDesc(projectId)
                .orElse(null);

        RiskAssessment risk = refreshRiskInternal(project, latestMilestone, latestFinancial);
        return toRiskAssessmentResponseDTO(risk);
    }

    // --- Helper methods ---

    private RiskAssessment refreshRiskInternal(Project project, Milestone latestMilestone, FinancialRecord latestFinancial) {
        Long projectId = project.getId();

        if (latestMilestone == null) {
            latestMilestone = milestoneRepository.findByProjectIdOrderByReportedDateDesc(projectId).stream().findFirst().orElse(null);
        }
        if (latestFinancial == null) {
            latestFinancial = financialRecordRepository.findTopByProjectIdOrderByReportedDateDescIdDesc(projectId).orElse(null);
        }

        // Previous risk assessment for trend-based detection
        RiskAssessment previousRisk = riskAssessmentRepository
                .findTopByProjectIdOrderByAssessedAtDescIdDesc(projectId)
                .orElse(null);

        List<String> riskDrivers = new ArrayList<>();
        double ruleScore = 0.0;

        // Rule 1: Financial physical mismatch
        if (latestMilestone != null && latestFinancial != null && project.getOriginalCost() != null && project.getOriginalCost().compareTo(BigDecimal.ZERO) > 0) {
            double financialPct = latestFinancial.getCumulativeExpenditure().doubleValue() / project.getOriginalCost().doubleValue() * 100.0;
            double physicalPct = latestMilestone.getActualPhysicalProgressPct() != null ? latestMilestone.getActualPhysicalProgressPct().doubleValue() : 0.0;
            double mismatch = financialPct - physicalPct;

            if (mismatch > 20.0) {
                ruleScore += 0.40;
                riskDrivers.add(String.format("Financial progress (%.1f%%) significantly exceeds physical progress (%.1f%%) by %.1f%%",
                        financialPct, physicalPct, mismatch));
            } else if (mismatch > 10.0) {
                ruleScore += 0.20;
                riskDrivers.add(String.format("Financial progress (%.1f%%) leads physical progress (%.1f%%)",
                        financialPct, physicalPct));
            }
        }

        // Rule 2: Schedule slip
        LocalDate originalComp = project.getOriginalCompletionDate();
        LocalDate revisedComp = project.getRevisedCompletionDate();
        if (originalComp != null && revisedComp != null && revisedComp.isAfter(originalComp)) {
            long slipMonths = ChronoUnit.MONTHS.between(originalComp, revisedComp);
            if (slipMonths > 6) {
                ruleScore += 0.35;
                riskDrivers.add(String.format("Schedule slip of %d months recorded against original timeline", slipMonths));
            } else if (slipMonths > 0) {
                ruleScore += 0.15;
                riskDrivers.add(String.format("Schedule slip of %d months recorded", slipMonths));
            }
        }

        // Rule 3: Cost overrun
        if (project.getOriginalCost() != null && project.getRevisedCost() != null && project.getRevisedCost().compareTo(project.getOriginalCost()) > 0) {
            double overrunPct = (project.getRevisedCost().doubleValue() - project.getOriginalCost().doubleValue()) / project.getOriginalCost().doubleValue() * 100.0;
            if (overrunPct > 15.0) {
                ruleScore += 0.30;
                riskDrivers.add(String.format("Cost escalation of %.1f%% over sanctioned budget", overrunPct));
            } else if (overrunPct > 0) {
                ruleScore += 0.15;
                riskDrivers.add(String.format("Cost revision of %.1f%%", overrunPct));
            }
        }

        // Rule 4: Physical progress gap
        if (latestMilestone != null && latestMilestone.getPlannedPhysicalProgressPct() != null && latestMilestone.getActualPhysicalProgressPct() != null) {
            double gap = latestMilestone.getPlannedPhysicalProgressPct().doubleValue() - latestMilestone.getActualPhysicalProgressPct().doubleValue();
            if (gap > 15.0) {
                ruleScore += 0.25;
                riskDrivers.add(String.format("Physical execution gap is %.1f%% behind targeted schedule", gap));
            }
        }

        // Baseline probability (capped at 1.0)
        double riskProbability = Math.min(1.0, Math.max(0.05, ruleScore));

        String riskBand;
        if (riskProbability >= 0.65) {
            riskBand = "HIGH";
        } else if (riskProbability >= 0.35) {
            riskBand = "MEDIUM";
        } else {
            riskBand = "LOW";
        }

        // Generate recommendations
        String recommendation = generateRecommendation(riskDrivers, riskBand);
        String topFeatures = riskDrivers.isEmpty() ? "Execution parameters currently within planned tolerance" : String.join("; ", riskDrivers);

        RiskAssessment assessment = new RiskAssessment();
        assessment.setProjectId(projectId);
        assessment.setAssessedAt(LocalDateTime.now());
        assessment.setRiskProbability(BigDecimal.valueOf(riskProbability * 100).setScale(2, RoundingMode.HALF_UP));
        assessment.setRiskBand(riskBand);
        assessment.setTopFeatures(topFeatures);
        assessment.setRecommendation(recommendation);

        RiskAssessment savedRisk = riskAssessmentRepository.save(assessment);

        // Trend-based alert generation: trigger if moved upward or newly HIGH
        boolean isUpwardTrend = false;
        if (previousRisk != null) {
            int prevLevel = getBandLevel(previousRisk.getRiskBand());
            int currentLevel = getBandLevel(riskBand);
            if (currentLevel > prevLevel) {
                isUpwardTrend = true;
            }
        } else if ("HIGH".equals(riskBand)) {
            isUpwardTrend = true;
        }

        if (isUpwardTrend || "HIGH".equals(riskBand)) {
            Alert alert = new Alert();
            alert.setProjectId(projectId);
            alert.setRiskAssessmentId(savedRisk.getId());
            alert.setSeverity("HIGH".equals(riskBand) ? "HIGH" : "MEDIUM");
            alert.setMessage(String.format("Early Warning: Project [%s] transitioned to %s risk. %s",
                    project.getName(), riskBand, riskDrivers.isEmpty() ? "Review progress metrics." : riskDrivers.get(0)));
            alert.setCreatedAt(LocalDateTime.now());
            alert.setResolved(false);
            alertRepository.save(alert);
        }

        return savedRisk;
    }

    private int getBandLevel(String band) {
        if (band == null) return 0;
        switch (band.toUpperCase()) {
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }

    private String generateRecommendation(List<String> drivers, String band) {
        if ("LOW".equals(band)) {
            return "Project is proceeding within standard schedule and budget bounds. Maintain standard periodic monitoring.";
        }

        StringBuilder rec = new StringBuilder();
        for (String driver : drivers) {
            if (driver.contains("Financial progress")) {
                rec.append("Recommend an immediate physical progress audit before authorizing further milestone disbursements. ");
            } else if (driver.contains("Schedule slip")) {
                rec.append("Recommend escalating project milestones to the inter-ministerial monitoring committee for right-of-way and statutory clearances. ");
            } else if (driver.contains("Cost escalation")) {
                rec.append("Recommend submitting revised DPR for CSPCC/Cabinet Committee review to audit contractor rates. ");
            } else if (driver.contains("gap")) {
                rec.append("Recommend contractor deploy additional resource shifts to recover execution deficit. ");
            }
        }

        if (rec.length() == 0) {
            rec.append("Schedule expedited officer review to evaluate pending milestones.");
        }

        return rec.toString().trim();
    }

    private void validateOfficerAssignment(Project project) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return;

        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            User assignedOfficer = project.getOfficer();
            if (assignedOfficer == null || !assignedOfficer.getUsername().equalsIgnoreCase(username)) {
                throw new AccessDeniedException("You are not assigned to this project");
            }
        }
    }

    private void mapRequestToProject(ProjectRequestDTO request, Project project) {
        project.setName(request.getName());
        project.setMinistryId(request.getMinistryId());
        project.setOriginalCost(request.getOriginalCost());
        project.setRevisedCost(request.getRevisedCost());
        project.setStartDate(request.getStartDate());
        project.setOriginalCompletion(request.getOriginalCompletionDate());
        project.setRevisedCompletion(request.getRevisedCompletionDate());
        project.setCurrentStatus(request.getCurrentStatus() != null ? request.getCurrentStatus() : "ON_TRACK");
        project.setState(request.getState());
        project.setImplementingAgency(request.getImplementingAgency());
    }

    private ProjectResponseDTO toProjectResponseDTO(Project project, Ministry ministry) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setMinistryId(project.getMinistryId());
        if (ministry != null) {
            dto.setMinistryName(ministry.getName());
            dto.setSector(ministry.getSector());
        }
        dto.setOriginalCost(project.getOriginalCost());
        dto.setRevisedCost(project.getRevisedCost());
        dto.setStartDate(project.getStartDate());
        dto.setOriginalCompletionDate(project.getOriginalCompletionDate());
        dto.setRevisedCompletionDate(project.getRevisedCompletionDate());
        dto.setCurrentStatus(project.getCurrentStatus());
        dto.setState(project.getState());
        dto.setImplementingAgency(project.getImplementingAgency());

        if (project.getOfficer() != null) {
            dto.setOfficerId(project.getOfficer().getId());
            dto.setOfficerUsername(project.getOfficer().getUsername());
        }

        // Latest milestone
        Milestone milestone = milestoneRepository.findByProjectIdOrderByReportedDateDesc(project.getId()).stream().findFirst().orElse(null);
        if (milestone != null) {
            dto.setLatestProgressPct(milestone.getActualPhysicalProgressPct());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isViewerOnly = false;
        if (auth != null && auth.isAuthenticated()) {
            isViewerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_VIEWER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_OFFICER"));
        }

        if (!isViewerOnly) {
            // Latest risk
            RiskAssessment risk = riskAssessmentRepository.findTopByProjectIdOrderByAssessedAtDescIdDesc(project.getId()).orElse(null);
            if (risk != null) {
                dto.setLatestRiskBand(risk.getRiskBand());
                dto.setLatestRiskProbability(risk.getRiskProbability());
            } else {
                dto.setLatestRiskBand("UNKNOWN");
            }

            // Active alerts count
            long activeAlerts = alertRepository.findByProjectIdAndResolved(project.getId(), false).size();
            dto.setActiveAlertsCount(activeAlerts);
        } else {
            dto.setLatestRiskBand(null);
            dto.setLatestRiskProbability(null);
            dto.setActiveAlertsCount(0);
        }

        return dto;
    }

    private RiskAssessmentResponseDTO toRiskAssessmentResponseDTO(RiskAssessment risk) {
        if (risk == null) return null;
        return new RiskAssessmentResponseDTO(
                risk.getId(),
                risk.getProjectId(),
                risk.getAssessedAt(),
                risk.getRiskProbability(),
                risk.getRiskBand(),
                risk.getTopFeatures(),
                risk.getRecommendation()
        );
    }
}