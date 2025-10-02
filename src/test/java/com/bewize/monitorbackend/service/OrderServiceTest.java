package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.dto.order.OrderListDto;
import com.bewize.monitorbackend.mappers.OrderMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("getOrders returns mapped list")
    void getOrders_ok() {
        Order o1 = new Order(); o1.setId("o1");
        Order o2 = new Order(); o2.setId("o2");
        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));

        // dynamic mapping stub
        when(orderMapper.toOrderListDto(any(Order.class))).thenAnswer(inv -> {
            Order src = inv.getArgument(0);
            OrderListDto dto = new OrderListDto();
            dto.setId(src.getId());
            dto.setCode("CODE-" + src.getId());
            return dto;
        });

        List<OrderListDto> result = orderService.getOrders();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(OrderListDto::getId)
                .containsExactlyInAnyOrder("o1", "o2");
        assertThat(result).extracting(OrderListDto::getCode)
                .containsExactlyInAnyOrder("CODE-o1", "CODE-o2");

        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(2)).toOrderListDto(any(Order.class));
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("getOrders returns empty list when repository empty")
    void getOrders_empty() {
        when(orderRepository.findAll()).thenReturn(List.of());

        List<OrderListDto> result = orderService.getOrders();

        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findAll();
        verifyNoInteractions(orderMapper); // mapper never called
    }
}
