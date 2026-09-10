package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.client.MlClient;
import com.infrastructure.monitoring.dto.MlPredictionResponseDTO;
import com.infrastructure.monitoring.dto.MlProjectFeaturesDTO;
import com.infrastructure.monitoring.entity.FinancialRecord;
import com.infrastructure.monitoring.entity.Milestone;
import com.infrastructure.monitoring.entity.Project;
import com.infrastructure.monitoring.exception.ResourceNotFoundException;
import com.infrastructure.monitoring.repository.FinancialRecordRepository;
import com.infrastructure.monitoring.repository.MilestoneRepository;
import com.infrastructure.monitoring.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RiskPredictionService {

    private static final Logger log = LoggerFactory.getLogger(RiskPredictionService.class);

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final MlClient mlClient;

    public RiskPredictionService(
            ProjectRepository projectRepository,
            MilestoneRepository milestoneRepository,
            FinancialRecordRepository financialRecordRepository,
            MlClient mlClient) {

        this.projectRepository = projectRepository;
        this.milestoneRepository = milestoneRepository;
        this.financialRecordRepository = financialRecordRepository;
        this.mlClient = mlClient;
    }

    public MlPredictionResponseDTO predictProjectRisk(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isOfficerOnly = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"))
                    && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isOfficerOnly) {
                String username = auth.getName();
                if (project.getOfficer() == null || !username.equalsIgnoreCase(project.getOfficer().getUsername())) {
                    throw new org.springframework.security.access.AccessDeniedException("You are not assigned to run risk predictions for this project");
                }
            }
        }

        List<Milestone> milestones = milestoneRepository.findByProjectIdOrderByReportedDateDesc(projectId);
        List<FinancialRecord> financialRecords = financialRecordRepository.findByProjectIdOrderByReportedDateDesc(projectId);

        MlProjectFeaturesDTO features = extractFeatures(project, milestones, financialRecords);
        return mlClient.predictRisk(features);
    }

    public MlProjectFeaturesDTO extractFeatures(
            Project project,
            List<Milestone> milestones,
            List<FinancialRecord> financialRecords) {

        // Reference Date T_ref = max(latestMilestone.reportedDate, latestFinancial.reportedDate)
        LocalDate latestMilestoneDate = (milestones != null && !milestones.isEmpty()) ? milestones.get(0).getReportedDate() : null;
        LocalDate latestFinancialDate = (financialRecords != null && !financialRecords.isEmpty()) ? financialRecords.get(0).getReportedDate() : null;

        LocalDate referenceDate;
        if (latestMilestoneDate != null && latestFinancialDate != null) {
            referenceDate = latestMilestoneDate.isAfter(latestFinancialDate) ? latestMilestoneDate : latestFinancialDate;
        } else if (latestMilestoneDate != null) {
            referenceDate = latestMilestoneDate;
        } else if (latestFinancialDate != null) {
            referenceDate = latestFinancialDate;
        } else if (project.getStartDate() != null) {
            referenceDate = project.getStartDate();
        } else {
            referenceDate = LocalDate.now();
        }

        // 1. physical_progress_pct
        Milestone m0 = (milestones != null && !milestones.isEmpty()) ? milestones.get(0) : null;
        double physicalProgressPct = 0.0;
        if (m0 != null && m0.getActualPercent() != null) {
            physicalProgressPct = Math.min(100.0, Math.max(0.0, m0.getActualPercent().doubleValue()));
        }

        // 2. progress_change_1m
        double progressChange1m = 0.0;
        if (milestones != null && milestones.size() >= 2) {
            Milestone m1 = milestones.get(1);
            if (m0 != null && m0.getActualPercent() != null && m1.getActualPercent() != null) {
                progressChange1m = m0.getActualPercent().doubleValue() - m1.getActualPercent().doubleValue();
            }
        }

        // 3. progress_change_2m
        double progressChange2m = 0.0;
        if (milestones != null && milestones.size() >= 3) {
            Milestone m2 = milestones.get(2);
            if (m0 != null && m0.getActualPercent() != null && m2.getActualPercent() != null) {
                progressChange2m = m0.getActualPercent().doubleValue() - m2.getActualPercent().doubleValue();
            }
        } else if (milestones != null && milestones.size() == 2) {
            progressChange2m = progressChange1m;
        }

        // 4. recent_progress_stalled
        int recentProgressStalled = (progressChange1m <= 0.0) ? 1 : 0;

        // 5. is_first_report
        int milestoneCount = (milestones != null) ? milestones.size() : 0;
        int financialCount = (financialRecords != null) ? financialRecords.size() : 0;
        int isFirstReport = (milestoneCount <= 1 && financialCount <= 1) ? 1 : 0;

        // Conversion constant: 1 Crore INR = 10,000,000 INR
        final double INR_TO_CRORE = 10_000_000.0;

        // Effective Cost in raw INR (revised_cost fallback to original_cost)
        double originalCost = (project.getOriginalCost() != null) ? project.getOriginalCost().doubleValue() : 0.0;
        double revisedCost = (project.getRevisedCost() != null) ? project.getRevisedCost().doubleValue() : 0.0;
        double costEff = (revisedCost > 0.0) ? revisedCost : (originalCost > 0.0 ? originalCost : 0.0);
        double costEffCrore = costEff / INR_TO_CRORE;

        // 6. expenditure_ratio (dimensionless ratio)
        FinancialRecord f0 = (financialRecords != null && !financialRecords.isEmpty()) ? financialRecords.get(0) : null;
        double cumulativeExpenditure = (f0 != null && f0.getCumulativeExpenditure() != null) ? f0.getCumulativeExpenditure().doubleValue() : 0.0;
        double cumulativeExpenditureCrore = cumulativeExpenditure / INR_TO_CRORE;
        double expenditureRatio = (costEff > 0.0) ? (cumulativeExpenditure / costEff) : 0.0;

        // 7. expenditure_change_1m (converted to Crore INR)
        double expenditureChange1m = 0.0;
        if (financialRecords != null && financialRecords.size() >= 2) {
            FinancialRecord f1 = financialRecords.get(1);
            if (f0 != null && f0.getCumulativeExpenditure() != null && f1.getCumulativeExpenditure() != null) {
                expenditureChange1m = Math.max(0.0, (f0.getCumulativeExpenditure().doubleValue() - f1.getCumulativeExpenditure().doubleValue()) / INR_TO_CRORE);
            }
        } else if (f0 != null) {
            if (f0.getNewExpenditure() != null && f0.getNewExpenditure().compareTo(BigDecimal.ZERO) > 0) {
                expenditureChange1m = f0.getNewExpenditure().doubleValue() / INR_TO_CRORE;
            } else if (f0.getCumulativeExpenditure() != null) {
                expenditureChange1m = Math.max(0.0, f0.getCumulativeExpenditure().doubleValue() / INR_TO_CRORE);
            }
        }

        // 8. cost_overrun_pct = (revised - original) / original (dimensionless ratio, NOT multiplied by 100)
        double costOverrunPct = 0.0;
        if (originalCost > 0.0 && revisedCost > 0.0) {
            costOverrunPct = (revisedCost - originalCost) / originalCost;
        }

        // 9. cost_overrun_crore (converted to Crore INR)
        double costOverrunCrore = 0.0;
        if (originalCost > 0.0 && revisedCost > 0.0) {
            costOverrunCrore = Math.max(0.0, (revisedCost - originalCost) / INR_TO_CRORE);
        }

        // 10. progress_spend_divergence (dimensionless)
        double progressSpendDivergence = (expenditureRatio * 100.0) - physicalProgressPct;

        // 11. months_since_approval
        double monthsSinceApproval = 36.0;
        if (project.getStartDate() != null) {
            long daysSinceApproval = ChronoUnit.DAYS.between(project.getStartDate(), referenceDate);
            monthsSinceApproval = Math.max(0.0, (double) daysSinceApproval / 30.4375);
        }

        // 12. months_to_commissioning
        LocalDate targetCompletion = project.getRevisedCompletion() != null ? project.getRevisedCompletion() : project.getOriginalCompletion();
        double monthsToCommissioning = 24.0;
        if (targetCompletion != null) {
            long daysToCompletion = ChronoUnit.DAYS.between(referenceDate, targetCompletion);
            monthsToCommissioning = (double) daysToCompletion / 30.4375;
        }

        // 13. is_overdue
        int isOverdue = (monthsToCommissioning < 0.0) ? 1 : 0;

        // 14. schedule_delay_months
        double scheduleDelayMonths = 0.0;
        if (project.getOriginalCompletion() != null && project.getRevisedCompletion() != null && project.getRevisedCompletion().isAfter(project.getOriginalCompletion())) {
            long delayDays = ChronoUnit.DAYS.between(project.getOriginalCompletion(), project.getRevisedCompletion());
            scheduleDelayMonths = Math.max(0.0, (double) delayDays / 30.4375);
        }

        // 15. required_monthly_velocity
        double remainingProgress = Math.max(0.0, 100.0 - physicalProgressPct);
        double requiredMonthlyVelocity = 0.0;
        if (remainingProgress <= 0.0) {
            requiredMonthlyVelocity = 0.0;
        } else if (monthsToCommissioning > 0.0) {
            requiredMonthlyVelocity = remainingProgress / monthsToCommissioning;
        } else {
            requiredMonthlyVelocity = remainingProgress;
        }

        // 16. velocity_gap
        double velocityGap = requiredMonthlyVelocity - progressChange1m;

        // 17. log_revised_cost = ln(1 + costEffCrore)
        double logRevisedCost = Math.log(1.0 + Math.max(0.0, costEffCrore));

        // 18. log_expenditure = ln(1 + cumulativeExpenditureCrore)
        double logExpenditure = Math.log(1.0 + Math.max(0.0, cumulativeExpenditureCrore));

        // 19. is_mega_project (cost >= 1000 Crore INR)
        int isMegaProject = (costEffCrore >= 1000.0) ? 1 : 0;

        // 20. agency_freq
        double agencyFreq = 1.0;
        if (project.getImplementingAgency() != null && !project.getImplementingAgency().isBlank()) {
            long count = projectRepository.countByImplementingAgency(project.getImplementingAgency());
            agencyFreq = Math.max(1.0, (double) count);
        }

        // 21. month_num
        int monthNum = referenceDate.getMonthValue();

        return new MlProjectFeaturesDTO(
                physicalProgressPct,
                progressChange1m,
                progressChange2m,
                recentProgressStalled,
                isFirstReport,
                expenditureRatio,
                expenditureChange1m,
                costOverrunPct,
                costOverrunCrore,
                progressSpendDivergence,
                monthsSinceApproval,
                monthsToCommissioning,
                isOverdue,
                scheduleDelayMonths,
                requiredMonthlyVelocity,
                velocityGap,
                logRevisedCost,
                logExpenditure,
                isMegaProject,
                agencyFreq,
                monthNum
        );
    }
}
