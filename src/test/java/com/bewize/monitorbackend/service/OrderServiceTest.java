package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.order.OrderListDto;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.projection.OrderListProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, null); // mapper not used in new implementation
    }

    private OrderListProjection projection(String id) {
        return new OrderListProjection() {
            public String getId() { return id; }
            public String getCode() { return "CODE-" + id; }
            public com.bewize.monitorbackend.enums.OrderType getType() { return null; }
            public com.bewize.monitorbackend.enums.OrderStatus getStatus() { return null; }
            public com.bewize.monitorbackend.enums.PlanType getPlanType() { return null; }
            public LocalDateTime getDate() { return null; }
            public Float getAmount() { return null; }
            public String getTransactionId() { return null; }
            public SubscriptionProjection getSubscription() { return null; }
            public StudentProjection getStudent() { return null; }
            public DiscountProjection getDiscount() { return null; }
        }; }

    @Test
    @DisplayName("getOrders returns mapped page response")
    void getOrders_paged_ok() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<OrderListProjection> page = new PageImpl<>(List.of(projection("o1"), projection("o2")), pageable, 2);
        when(orderRepository.findAllProjectedBy(pageable)).thenReturn(page);

        PageResponse<OrderListDto> resp = orderService.getOrders(pageable);

        assertThat(resp.getData()).hasSize(2);
        assertThat(resp.getData()).extracting(OrderListDto::getId).containsExactlyInAnyOrder("o1", "o2");
        assertThat(resp.getMeta().getPage()).isEqualTo(0);
        assertThat(resp.getMeta().getSize()).isEqualTo(2);
        assertThat(resp.getMeta().getTotalElements()).isEqualTo(2);
        assertThat(resp.getMeta().getTotalPages()).isEqualTo(1);

        verify(orderRepository).findAllProjectedBy(pageable);
    }

    @Test
    @DisplayName("getOrders returns empty page response when no data")
    void getOrders_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<OrderListProjection> page = new PageImpl<>(List.of(), pageable, 0);
        when(orderRepository.findAllProjectedBy(pageable)).thenReturn(page);

        PageResponse<OrderListDto> resp = orderService.getOrders(pageable);

        assertThat(resp.getData()).isEmpty();
        assertThat(resp.getMeta().getTotalElements()).isEqualTo(0);
        assertThat(resp.getMeta().getTotalPages()).isEqualTo(0);
        verify(orderRepository).findAllProjectedBy(pageable);
    }
}
