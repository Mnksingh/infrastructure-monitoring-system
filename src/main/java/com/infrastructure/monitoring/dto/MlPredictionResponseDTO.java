package com.infrastructure.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MlPredictionResponseDTO(
        @JsonProperty("risk_probability") double riskProbability,
        @JsonProperty("risk") boolean risk,
        @JsonProperty("risk_level") String riskLevel,
        @JsonProperty("threshold") double threshold,
        @JsonProperty("model_version") String modelVersion,
        @JsonProperty("explanations") List<MlExplanationDTO> explanations,
        @JsonProperty("prescriptive_actions") List<String> prescriptiveActions,
        @JsonProperty("early_warning") Boolean earlyWarning,
        @JsonProperty("early_warning_message") String earlyWarningMessage
) {}
