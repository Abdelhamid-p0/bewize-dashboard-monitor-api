package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.Dashboard.DashboardSummaryDto;
import com.bewize.monitorbackend.dto.Dashboard.PerPeriodResponseDto;
import com.bewize.monitorbackend.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.bewize.monitorbackend.constants.ResourcesPaths.DASHBOARD;

@Tag(name = "Dashboard monitor admin Resource")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DASHBOARD)
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    public DashboardSummaryDto getMetrics() {
        return dashboardService.getGlobalMetrics();
    }

    @GetMapping("/students")
    public PerPeriodResponseDto getStudentsPerPeriod(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {

        if (month == null) {
            return dashboardService.getStudentsPerMonth(year);
        } else {
            return dashboardService.getStudentsPerDay(year, month);
        }
    }

    @GetMapping("/orders")
    public PerPeriodResponseDto getOrdersPerPeriod(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {

        if (month == null) return dashboardService.getOrdersPerMonth(year);
        return dashboardService.getOrdersPerDay(year, month);
    }

    @GetMapping("/subscriptions")
    public PerPeriodResponseDto getSubscriptionsPerPeriod(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {

        if (month == null) return dashboardService.getSubscriptionsPerMonth(year);
        return dashboardService.getSubscriptionsPerDay(year, month);
    }
}
