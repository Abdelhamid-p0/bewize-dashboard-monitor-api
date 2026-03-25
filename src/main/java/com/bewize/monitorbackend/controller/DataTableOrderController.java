package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.service.DataTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Data table orders Resource")
@RestController
@RequiredArgsConstructor
@RequestMapping("/datatable/orders")
public class DataTableOrderController {

    private final DataTableService dataTableService;

    @Operation(summary = "List orders for data table with fields and filters")
    @GetMapping
    public ResponseEntity<PageResponse<Map<String, Object>>> getOrders(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) List<String> fields,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String planType,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dataTableService.getOrderRows(pageable, fields, status, planType, search));
    }
}
