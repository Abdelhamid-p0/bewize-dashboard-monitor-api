package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.repository.DiscountRepository;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataTableFilterOptionsServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private DiscountRepository discountRepository;

    private DataTableFilterOptionsService service;

    @BeforeEach
    void setUp() {
        service = new DataTableFilterOptionsService(studentRepository, orderRepository, subscriptionRepository,
                discountRepository);
    }

    @Test
    @DisplayName("getStudentFilterOptions returns backend-owned options")
    void getStudentFilterOptions_ok() {
        when(studentRepository.findDistinctGenders()).thenReturn(List.of(Gender.FEMALE, Gender.MALE));
        when(studentRepository.findDistinctCycles()).thenReturn(List.of(Cycle.ELEMENTARY_SCHOOL, Cycle.MIDDLE_SCHOOL));
        when(studentRepository.findDistinctLevelNames()).thenReturn(List.of("CP", "CE1"));
        when(orderRepository.findDistinctPlanTypes()).thenReturn(List.of(PlanType.PREMIUM, PlanType.MONTH));

        var response = service.getStudentFilterOptions();

        assertThat(response).containsKeys("gender", "cycle", "level", "type", "planType");
        assertThat(response.get("gender")).extracting("value").containsExactly("FEMALE", "MALE");
        assertThat(response.get("cycle")).extracting("value").containsExactly("ELEMENTARY_SCHOOL", "MIDDLE_SCHOOL");
        assertThat(response.get("level")).extracting("value").containsExactly("CP", "CE1");
        assertThat(response.get("planType")).extracting("value").containsExactly("PREMIUM", "MONTH");
    }

    @Test
    @DisplayName("getOrderFilterOptions returns enum based options")
    void getOrderFilterOptions_ok() {
        when(orderRepository.findDistinctStatuses()).thenReturn(List.of(OrderStatus.PAID, OrderStatus.FREE));
        when(orderRepository.findDistinctPlanTypes()).thenReturn(List.of(PlanType.YEAR, PlanType.MONTH));

        var response = service.getOrderFilterOptions();

        assertThat(response).containsKeys("status", "planType");
        assertThat(response.get("status")).extracting("value").containsExactly("PAID", "FREE");
        assertThat(response.get("planType")).extracting("value").containsExactly("YEAR", "MONTH");
    }

    @Test
    @DisplayName("getSubscriptionFilterOptions returns enum based options")
    void getSubscriptionFilterOptions_ok() {
        when(subscriptionRepository.findDistinctOrderPlanTypes())
                .thenReturn(List.of(PlanType.SEMESTER, PlanType.MONTH));
        when(subscriptionRepository.existsByEndDateGreaterThanEqual(org.mockito.ArgumentMatchers.any()))
                .thenReturn(true);
        when(subscriptionRepository.existsByEndDateLessThan(org.mockito.ArgumentMatchers.any()))
                .thenReturn(true);

        var response = service.getSubscriptionFilterOptions();

        assertThat(response).containsKeys("status", "type", "planType");
        assertThat(response.get("status")).extracting("value").containsExactly("ACTIVE", "INACTIVE");
        assertThat(response.get("type")).extracting("value").containsExactly("SEMESTER", "MONTH");
    }

    @Test
    @DisplayName("getDiscountFilterOptions returns repository derived options")
    void getDiscountFilterOptions_ok() {
        when(discountRepository.existsByEndDateGreaterThanEqual(org.mockito.ArgumentMatchers.any())).thenReturn(true);
        when(discountRepository.existsByEndDateLessThan(org.mockito.ArgumentMatchers.any())).thenReturn(true);
        when(discountRepository.findDistinctCodes()).thenReturn(List.of("DISC10", "DISC20"));
        when(discountRepository.findDistinctPercentages()).thenReturn(List.of(10, 20));

        var response = service.getDiscountFilterOptions();

        assertThat(response).containsKeys("active", "code", "percentage");
        assertThat(response.get("code")).extracting("value").containsExactly("DISC10", "DISC20");
        assertThat(response.get("percentage")).extracting("value").containsExactly("10", "20");
    }
}
