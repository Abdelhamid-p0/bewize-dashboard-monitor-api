package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.dto.Dashboard.DashboardSummaryDto;
import com.bewize.monitorbackend.dto.Dashboard.MetricValueDto;
import com.bewize.monitorbackend.dto.Dashboard.PerPeriodResponseDto;
import com.bewize.monitorbackend.dto.Dashboard.TimePointDto;
import com.bewize.monitorbackend.mappers.DashboardMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DashboardMapper dashboardMapper;


    @Transactional(readOnly = true)
    public DashboardSummaryDto getGlobalMetrics() {
        long totalStudents = studentRepository.count();
        long totalOrders = orderRepository.count();
        long totalSubscriptions = subscriptionRepository.count();

        LocalDate now = LocalDate.now();
        LocalDate prev = now.minusMonths(1);

        Double studentsGrowth = computeGrowth(
                studentRepository.countByYearAndMonth(prev.getYear(), prev.getMonthValue()),
                studentRepository.countByYearAndMonth(now.getYear(), now.getMonthValue())
        );
        Double ordersGrowth = computeGrowth(
                orderRepository.countByYearAndMonth(prev.getYear(), prev.getMonthValue()),
                orderRepository.countByYearAndMonth(now.getYear(), now.getMonthValue())
        );
        Double subsGrowth = computeGrowth(
                subscriptionRepository.countByYearAndMonth(prev.getYear(), prev.getMonthValue()),
                subscriptionRepository.countByYearAndMonth(now.getYear(), now.getMonthValue())
        );

        return new DashboardSummaryDto(
                new MetricValueDto(totalStudents, studentsGrowth),
                new MetricValueDto(totalOrders, ordersGrowth),
                new MetricValueDto(totalSubscriptions, subsGrowth)
        );
    }

    @Transactional(readOnly = true)
    public PerPeriodResponseDto getStudentsPerMonth(int year) {
        List<DashboardMonthCountProjection> rows = studentRepository.findStudentCountsByMonth(year);
        List<TimePointDto> months = dashboardMapper.monthProjectionListToDtoList(rows);
        long total = months.stream().mapToLong(TimePointDto::getCount).sum();
        return new PerPeriodResponseDto(months, total);
    }

    @Transactional(readOnly = true)
    public PerPeriodResponseDto getStudentsPerDay(int year, int month) {
        List<DashboardDayCountProjection> rows = studentRepository.findStudentCountsByDay(year, month);
        List<TimePointDto> days = dashboardMapper.dayProjectionListToDtoList(rows);
        long total = days.stream().mapToLong(TimePointDto::getCount).sum();
        return new PerPeriodResponseDto(days, total);
    }

    @Transactional(readOnly = true)
    public PerPeriodResponseDto getOrdersPerMonth(int year) {
        List<DashboardMonthCountProjection> rows = orderRepository.findOrderCountsByMonth(year);
        List<TimePointDto> months = dashboardMapper.monthProjectionListToDtoList(rows);
        long total = months.stream().mapToLong(TimePointDto::getCount).sum();
        return new PerPeriodResponseDto(months, total);
    }

    @Transactional(readOnly = true)
    public PerPeriodResponseDto getOrdersPerDay(int year, int month) {
        List<DashboardDayCountProjection> rows = orderRepository.findOrderCountsByDay(year, month);
        List<TimePointDto> days = dashboardMapper.dayProjectionListToDtoList(rows);
        long total = days.stream().mapToLong(TimePointDto::getCount).sum();
        return new PerPeriodResponseDto(days, total);
    }

    @Transactional(readOnly = true)
    public PerPeriodResponseDto getSubscriptionsPerMonth(int year) {
        List<DashboardMonthCountProjection> rows = subscriptionRepository.findSubscriptionCountsByMonth(year);
        List<TimePointDto> months = dashboardMapper.monthProjectionListToDtoList(rows);
        long total = months.stream().mapToLong(TimePointDto::getCount).sum();
        return new PerPeriodResponseDto(months, total);
    }

    @Transactional(readOnly = true)
    public PerPeriodResponseDto getSubscriptionsPerDay(int year, int month) {
        List<DashboardDayCountProjection> rows = subscriptionRepository.findSubscriptionCountsByDay(year, month);
        List<TimePointDto> days = dashboardMapper.dayProjectionListToDtoList(rows);
        long total = days.stream().mapToLong(TimePointDto::getCount).sum();
        return new PerPeriodResponseDto(days, total);
    }


    private Double computeGrowth(long previousMonthCount, long currentMonthCount) {
        if (previousMonthCount == 0) {
            return (currentMonthCount == 0) ? 0.0 : null; // null = new
        }
        return ((double)(currentMonthCount - previousMonthCount) / previousMonthCount) * 100;
    }
}
