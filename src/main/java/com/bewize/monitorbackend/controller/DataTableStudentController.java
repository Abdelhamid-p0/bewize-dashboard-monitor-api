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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Data table students Resource")
@RestController
@RequiredArgsConstructor
@RequestMapping("/datatable/students")
public class DataTableStudentController {

    private final DataTableService dataTableService;
    private final DataTableFilterOptionsService filterOptionsService;

    @Operation(summary = "List students for data table with fields and filters")
    @GetMapping
    public ResponseEntity<PageResponse<Map<String, Object>>> getStudents(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) List<String> fields,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String cycle,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String planType) {
        return ResponseEntity
                .ok(dataTableService.getStudentRows(pageable, fields, search, gender, cycle, level, type, planType));
    }

    @Operation(summary = "Get student filter options")
    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<FilterOptionDto>>> getStudentFilters() {
        return ResponseEntity.ok(filterOptionsService.getStudentFilterOptions());
    }
}
