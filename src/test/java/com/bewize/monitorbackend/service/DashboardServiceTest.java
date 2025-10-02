package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.dto.Dashboard.DashboardSummaryDto;
import com.bewize.monitorbackend.dto.Dashboard.PerPeriodResponseDto;
import com.bewize.monitorbackend.dto.Dashboard.TimePointDto;
import com.bewize.monitorbackend.mappers.DashboardMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private DashboardMapper dashboardMapper;

    private DashboardService service;

    private void initService() {
        service = new DashboardService(studentRepository, orderRepository, subscriptionRepository, dashboardMapper);
    }

    // Simple projection stubs
    private DashboardMonthCountProjection monthProj(String label, long count) {
        return new DashboardMonthCountProjection() {
            public String getLabel() { return label; }
            public Long getCount() { return count; }
        }; }
    private DashboardDayCountProjection dayProj(String label, long count) {
        return new DashboardDayCountProjection() {
            public String getLabel() { return label; }
            public Long getCount() { return count; }
        }; }

    @Test
    @DisplayName("getGlobalMetrics returns totals and growth with null for new growth")
    void getGlobalMetrics_newGrowth() {
        initService();
        // totals
        when(studentRepository.count()).thenReturn(5L);
        when(orderRepository.count()).thenReturn(8L);
        when(subscriptionRepository.count()).thenReturn(3L);

        LocalDate now = LocalDate.now();
        LocalDate prev = now.minusMonths(1);

        // growth: previous 0, current >0 => null (new)
        when(studentRepository.countByYearAndMonth(prev.getYear(), prev.getMonthValue())).thenReturn(0L);
        when(studentRepository.countByYearAndMonth(now.getYear(), now.getMonthValue())).thenReturn(5L);

        when(orderRepository.countByYearAndMonth(prev.getYear(), prev.getMonthValue())).thenReturn(4L);
        when(orderRepository.countByYearAndMonth(now.getYear(), now.getMonthValue())).thenReturn(8L); // growth 100%

        when(subscriptionRepository.countByYearAndMonth(prev.getYear(), prev.getMonthValue())).thenReturn(0L);
        when(subscriptionRepository.countByYearAndMonth(now.getYear(), now.getMonthValue())).thenReturn(0L); // stays 0 => 0.0

        DashboardSummaryDto dto = service.getGlobalMetrics();

        assertThat(dto.getStudents().getCount()).isEqualTo(5);
        assertThat(dto.getStudents().getGrowthPct()).isNull(); // new
        assertThat(dto.getOrders().getCount()).isEqualTo(8);
        assertThat(dto.getOrders().getGrowthPct()).isEqualTo(100.0);
        assertThat(dto.getSubscriptions().getCount()).isEqualTo(3);
        assertThat(dto.getSubscriptions().getGrowthPct()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getStudentsPerMonth aggregates total from mapper list")
    void getStudentsPerMonth_ok() {
        initService();
        int year = 2025;
        List<DashboardMonthCountProjection> raw = List.of(monthProj("2025-09", 2), monthProj("2025-10", 3));
        when(studentRepository.findStudentCountsByMonth(year)).thenReturn(raw);
        List<TimePointDto> mapped = List.of(new TimePointDto("2025-09",2), new TimePointDto("2025-10",3));
        when(dashboardMapper.monthProjectionListToDtoList(raw)).thenReturn(mapped);

        PerPeriodResponseDto resp = service.getStudentsPerMonth(year);

        assertThat(resp.getPoints()).hasSize(2);
        assertThat(resp.getTotal()).isEqualTo(5);
        assertThat(resp.getPoints()).extracting(TimePointDto::getLabel).containsExactly("2025-09","2025-10");
        verify(studentRepository).findStudentCountsByMonth(year);
        verify(dashboardMapper).monthProjectionListToDtoList(raw);
    }

    @Test
    @DisplayName("getSubscriptionsPerDay aggregates daily counts")
    void getSubscriptionsPerDay_ok() {
        initService();
        int year = 2025, month = 10;
        List<DashboardDayCountProjection> raw = List.of(dayProj("2025-10-01",1), dayProj("2025-10-02",0), dayProj("2025-10-03",4));
        when(subscriptionRepository.findSubscriptionCountsByDay(year, month)).thenReturn(raw);
        List<TimePointDto> mapped = List.of(new TimePointDto("2025-10-01",1), new TimePointDto("2025-10-02",0), new TimePointDto("2025-10-03",4));
        when(dashboardMapper.dayProjectionListToDtoList(raw)).thenReturn(mapped);

        PerPeriodResponseDto resp = service.getSubscriptionsPerDay(year, month);

        assertThat(resp.getTotal()).isEqualTo(5);
        assertThat(resp.getPoints()).hasSize(3);
        assertThat(resp.getPoints()).extracting(TimePointDto::getCount).containsExactly(1L,0L,4L);
        verify(subscriptionRepository).findSubscriptionCountsByDay(year, month);
        verify(dashboardMapper).dayProjectionListToDtoList(raw);
    }

    @Test
    @DisplayName("getOrdersPerMonth passes repository result into mapper")
    void getOrdersPerMonth_mapperInvocation() {
        initService();
        int year = 2024;
        List<DashboardMonthCountProjection> raw = List.of();
        when(orderRepository.findOrderCountsByMonth(year)).thenReturn(raw);
        when(dashboardMapper.monthProjectionListToDtoList(raw)).thenReturn(List.of());

        PerPeriodResponseDto resp = service.getOrdersPerMonth(year);

        assertThat(resp.getPoints()).isEmpty();
        assertThat(resp.getTotal()).isZero();
        verify(orderRepository).findOrderCountsByMonth(year);
        verify(dashboardMapper).monthProjectionListToDtoList(raw);
    }
}
