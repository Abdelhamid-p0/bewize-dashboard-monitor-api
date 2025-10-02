package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.student.StudentDetailsDto;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        StudentController controller = new StudentController(studentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /students returns list")
    void getStudents_ok() throws Exception {
        StudentListDto s1 = new StudentListDto();
        s1.setId("stu-1");
        s1.setFirstName("Ana");
        s1.setLastName("Doe");
        s1.setEmail("ana@example.com");

        StudentListDto s2 = new StudentListDto();
        s2.setId("stu-2");
        s2.setFirstName("Bob");

        when(studentService.getStudents()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("stu-1")))
                .andExpect(jsonPath("$[0].firstName", is("Ana")))
                .andExpect(jsonPath("$[1].id", is("stu-2")));
    }

    @Test
    @DisplayName("GET /students/{id} returns details")
    void getStudentById_ok() throws Exception {
        StudentDetailsDto details = new StudentDetailsDto();
        details.setId("stu-42");
        details.setFirstName("Zoe");
        details.setEmail("zoe@example.com");

        when(studentService.getStudentDetails("stu-42")).thenReturn(details);

        mockMvc.perform(get("/students/{id}", "stu-42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("stu-42")))
                .andExpect(jsonPath("$.firstName", is("Zoe")))
                .andExpect(jsonPath("$.email", is("zoe@example.com")));
    }
}
