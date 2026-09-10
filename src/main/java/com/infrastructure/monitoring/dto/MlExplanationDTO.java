package com.infrastructure.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MlExplanationDTO(
        @JsonProperty("feature") String feature,
        @JsonProperty("contribution") double contribution,
        @JsonProperty("direction") String direction,
        @JsonProperty("human_readable_reason") String humanReadableReason
) {}
