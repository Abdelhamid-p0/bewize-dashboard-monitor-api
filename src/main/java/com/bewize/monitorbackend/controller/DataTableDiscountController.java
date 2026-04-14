package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.datatable.FilterOptionDto;
import com.bewize.monitorbackend.service.DataTableService;
import com.bewize.monitorbackend.service.DataTableFilterOptionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Data table discounts Resource")
@RestController
@RequiredArgsConstructor
@RequestMapping("/datatable/discounts")
public class DataTableDiscountController {

    private final DataTableService dataTableService;
    private final DataTableFilterOptionsService filterOptionsService;

    @Operation(summary = "List discounts for data table with fields and filters")
    @GetMapping
    public ResponseEntity<PageResponse<Map<String, Object>>> getDiscounts(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) List<String> fields,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String percentage,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dataTableService.getDiscountRows(
                pageable,
                fields,
                active,
                code,
                percentage,
                search,
                startDate,
                endDate));
    }

    @Operation(summary = "Get discount filter options")
    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<FilterOptionDto>>> getDiscountFilters() {
        return ResponseEntity.ok(filterOptionsService.getDiscountFilterOptions());
    }
}
