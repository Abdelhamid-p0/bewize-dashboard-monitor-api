package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.discount.DiscountDto;
import com.bewize.monitorbackend.dto.order.OrderListDto;
import com.bewize.monitorbackend.dto.student.StudentDto;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.OrderType;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        OrderController controller = new OrderController(orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private OrderListDto order(String id, String code, String studentId, String discountId) {
        OrderListDto o = new OrderListDto();
        o.setId(id);
        o.setCode(code);
        o.setType(OrderType.CARD);
        o.setStatus(OrderStatus.PAID);
        o.setPlanType(PlanType.MONTH);
        o.setDate(LocalDateTime.parse("2024-01-01T00:00:00"));
        o.setAmount(49.99f);
        StudentDto s = new StudentDto();
        s.setId(studentId);
        s.setFirstName("John");
        o.setStudent(s);
        DiscountDto d = new DiscountDto();
        d.setId(discountId);
        d.setPercentage(10);
        o.setDiscount(d);
        return o;
    }

    @Test
    @DisplayName("GET /orders returns list")
    void getOrders_ok() throws Exception {
        List<OrderListDto> payload = List.of(
                order("ord-1", "O-001", "stu-1", "disc-1"),
                order("ord-2", "O-002", "stu-2", "disc-2")
        );
        when(orderService.getOrders()).thenReturn(payload);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("ord-1")))
                .andExpect(jsonPath("$[0].code", is("O-001")))
                .andExpect(jsonPath("$[0].student.id", is("stu-1")))
                .andExpect(jsonPath("$[0].discount.id", is("disc-1")))
                .andExpect(jsonPath("$[1].id", is("ord-2")));
    }
}
