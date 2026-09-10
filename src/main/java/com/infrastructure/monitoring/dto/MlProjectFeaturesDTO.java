package com.infrastructure.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MlProjectFeaturesDTO(
        @JsonProperty("physical_progress_pct") double physicalProgressPct,
        @JsonProperty("progress_change_1m") double progressChange1m,
        @JsonProperty("progress_change_2m") double progressChange2m,
        @JsonProperty("recent_progress_stalled") int recentProgressStalled,
        @JsonProperty("is_first_report") int isFirstReport,
        @JsonProperty("expenditure_ratio") double expenditureRatio,
        @JsonProperty("expenditure_change_1m") double expenditureChange1m,
        @JsonProperty("cost_overrun_pct") double costOverrunPct,
        @JsonProperty("cost_overrun_crore") double costOverrunCrore,
        @JsonProperty("progress_spend_divergence") double progressSpendDivergence,
        @JsonProperty("months_since_approval") double monthsSinceApproval,
        @JsonProperty("months_to_commissioning") double monthsToCommissioning,
        @JsonProperty("is_overdue") int isOverdue,
        @JsonProperty("schedule_delay_months") double scheduleDelayMonths,
        @JsonProperty("required_monthly_velocity") double requiredMonthlyVelocity,
        @JsonProperty("velocity_gap") double velocityGap,
        @JsonProperty("log_revised_cost") double logRevisedCost,
        @JsonProperty("log_expenditure") double logExpenditure,
        @JsonProperty("is_mega_project") int isMegaProject,
        @JsonProperty("agency_freq") double agencyFreq,
        @JsonProperty("month_num") int monthNum
) {}
