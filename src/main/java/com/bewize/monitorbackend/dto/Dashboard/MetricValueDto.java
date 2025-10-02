package com.bewize.monitorbackend.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MetricValueDto {
    private long count;        // total count in current period
    private Double growthPct;  // percent change vs previous period (null when undefined)
}

