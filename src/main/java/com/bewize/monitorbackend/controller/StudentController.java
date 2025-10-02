package com.bewize.monitorbackend.controller;


import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.dto.student.StudentDetailsDto;
import com.bewize.monitorbackend.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.bewize.monitorbackend.constants.ResourcesPaths.STUDENTS;

@Tag(name = "Student monitor admin Resource")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(STUDENTS)
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "List all students")
    @GetMapping
    public ResponseEntity<PageResponse<StudentListDto>> getStudents(
            @ParameterObject Pageable pageable) {

        PageResponse<StudentListDto> page = studentService.getStudents(pageable);
        log.info("students page loaded: page={} size={} total={}", page.getMeta().getPage(), page.getMeta().getSize(), page.getMeta().getTotalElements());
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get student details")
    @GetMapping("/{studentId}")
    public StudentDetailsDto getStudentById(@PathVariable String studentId) {
        StudentDetailsDto student = studentService.getStudentDetails(studentId);
        log.info("student details loaded: {}", student);
        return student;
    }

}
