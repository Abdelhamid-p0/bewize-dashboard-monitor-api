package com.bewize.monitorbackend.controller;


import com.bewize.monitorbackend.dto.Dashboard.PerPeriodResponseDto;
import com.bewize.monitorbackend.dto.Dashboard.TimePointDto;
import com.bewize.monitorbackend.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.util.List.of;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        DashboardController controller = new DashboardController(dashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // helper to build TimePointDto quickly
    private TimePointDto tp(String label, long count) {
        return new TimePointDto(label, count);
    }

    @Test
    @DisplayName("GET /dashboard/students?year=2025 returns months list")
    void getStudentsPerYear_ok() throws Exception {
        // prepare fake months (only 3 months shown for brevity)
        List<TimePointDto> months = of(
                tp("2025-01", 5),
                tp("2025-02", 10),
                tp("2025-03", 8)
        );
        PerPeriodResponseDto payload = new PerPeriodResponseDto(months, 23L);

        when(dashboardService.getStudentsPerMonth(2025)).thenReturn(payload);

        mockMvc.perform(get("/dashboard/students").param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", hasSize(3)))
                .andExpect(jsonPath("$.points[0].label", is("2025-01")))
                .andExpect(jsonPath("$.points[0].count", is(5)))
                .andExpect(jsonPath("$.points[1].label", is("2025-02")))
                .andExpect(jsonPath("$.total", is(23)));
    }

    @Test
    @DisplayName("GET /dashboard/students?year=2025&month=9 returns days list")
    void getStudentsPerMonth_ok() throws Exception {
        List<TimePointDto> days = of(
                tp("2025-09-01", 2),
                tp("2025-09-02", 1),
                tp("2025-09-03", 0)
        );
        PerPeriodResponseDto payload = new PerPeriodResponseDto(days, 3L);

        when(dashboardService.getStudentsPerDay(2025, 9)).thenReturn(payload);

        mockMvc.perform(get("/dashboard/students")
                        .param("year", "2025")
                        .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", hasSize(3)))
                .andExpect(jsonPath("$.points[0].label", is("2025-09-01")))
                .andExpect(jsonPath("$.points[0].count", is(2)))
                .andExpect(jsonPath("$.total", is(3)));
    }

    @Test
    @DisplayName("GET /dashboard/orders?year=2025 returns months list")
    void getOrdersPerYear_ok() throws Exception {
        List<TimePointDto> months = of(
                tp("2025-01", 12),
                tp("2025-02", 7)
        );
        PerPeriodResponseDto payload = new PerPeriodResponseDto(months, 19L);

        when(dashboardService.getOrdersPerMonth(2025)).thenReturn(payload);

        mockMvc.perform(get("/dashboard/orders").param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", hasSize(2)))
                .andExpect(jsonPath("$.points[0].label", is("2025-01")))
                .andExpect(jsonPath("$.points[0].count", is(12)))
                .andExpect(jsonPath("$.total", is(19)));
    }

    @Test
    @DisplayName("GET /dashboard/subscriptions?year=2025&month=9 returns days list")
    void getSubscriptionsPerMonth_ok() throws Exception {
        List<TimePointDto> days = of(
                tp("2025-09-01", 0),
                tp("2025-09-02", 1),
                tp("2025-09-03", 2)
        );
        PerPeriodResponseDto payload = new PerPeriodResponseDto(days, 3L);

        when(dashboardService.getSubscriptionsPerDay(2025, 9)).thenReturn(payload);

        mockMvc.perform(get("/dashboard/subscriptions")
                        .param("year", "2025")
                        .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", hasSize(3)))
                .andExpect(jsonPath("$.points[1].label", is("2025-09-02")))
                .andExpect(jsonPath("$.points[1].count", is(1)))
                .andExpect(jsonPath("$.total", is(3)));
    }
}
