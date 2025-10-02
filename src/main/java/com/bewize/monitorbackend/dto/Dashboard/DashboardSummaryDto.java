package com.bewize.monitorbackend.dto.Dashboard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DashboardSummaryDto {
    private MetricValueDto students;
    private MetricValueDto orders;
    private MetricValueDto subscriptions;

}

