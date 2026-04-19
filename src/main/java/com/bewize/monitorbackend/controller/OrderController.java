package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.order.OrderListDto;
import com.bewize.monitorbackend.service.OrderService;
import com.bewize.monitorbackend.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.bewize.monitorbackend.constants.ResourcesPaths.ORDERS;

@Tag(name = "Order monitor admin Resource")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ORDERS)
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "List all orders")
    @GetMapping
    public ResponseEntity<PageResponse<OrderListDto>> getOrders(@ParameterObject Pageable pageable) {
        PageResponse<OrderListDto> page = orderService.getOrders(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get order details")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderListDto> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

}
