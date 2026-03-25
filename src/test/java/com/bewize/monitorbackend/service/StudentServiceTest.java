package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.order.OrderDto;
import com.bewize.monitorbackend.dto.student.StudentDetailsDto;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.mappers.OrderMapper;
import com.bewize.monitorbackend.mappers.StudentMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.projection.StudentListProjection;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private OrderMapper orderMapper;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, orderRepository, studentMapper, orderMapper);
    }

    @Test
    @DisplayName("getStudents returns mapped page response")
    void getStudents_paged_ok() {
        Pageable pageable = PageRequest.of(0, 2);

        // create simple projection implementations
        StudentListProjection p1 = new StudentListProjection() {
            public String getId() {
                return "s1";
            }

            public String getCne() {
                return "cne1";
            }

            public String getFirstName() {
                return "Ana";
            }

            public String getLastName() {
                return "Lee";
            }

            public String getEmail() {
                return "ana@example.com";
            }

            public String getPhone() {
                return "111";
            }

            public com.bewize.monitorbackend.enums.Gender getGender() {
                return null;
            }

            public java.time.LocalDate getsignupDate() {
                return null;
            }

            public StudentListProjection.LevelProjection getLevel() {
                return null;
            }
        };
        StudentListProjection p2 = new StudentListProjection() {
            public String getId() {
                return "s2";
            }

            public String getCne() {
                return "cne2";
            }

            public String getFirstName() {
                return "Bob";
            }

            public String getLastName() {
                return "Ray";
            }

            public String getEmail() {
                return "bob@example.com";
            }

            public String getPhone() {
                return "222";
            }

            public com.bewize.monitorbackend.enums.Gender getGender() {
                return null;
            }

            public java.time.LocalDate getsignupDate() {
                return null;
            }

            public StudentListProjection.LevelProjection getLevel() {
                return null;
            }
        };
        Page<StudentListProjection> page = new PageImpl<>(java.util.List.of(p1, p2), pageable, 2);
        when(studentRepository.findAllProjectedBy(pageable)).thenReturn(page);

        PageResponse<StudentListDto> response = studentService.getStudents(pageable);

        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData()).extracting(StudentListDto::getId).containsExactlyInAnyOrder("s1", "s2");
        assertThat(response.getMeta().getPage()).isEqualTo(0);
        assertThat(response.getMeta().getSize()).isEqualTo(2);
        assertThat(response.getMeta().getTotalElements()).isEqualTo(2);
        assertThat(response.getMeta().getTotalPages()).isEqualTo(1);

        verify(studentRepository).findAllProjectedBy(pageable);
    }

    @Test
    @DisplayName("getStudentDetails returns details with mapped orders")
    void getStudentDetails_ok() {
        String studentId = "stu-123";
        Student student = new Student();
        student.setId(studentId);
        student.setFirstName("Ana");
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Order o1 = new Order();
        o1.setId("o1");
        Order o2 = new Order();
        o2.setId("o2");
        when(orderRepository.findOrdersWithSubscriptionByStudentId(studentId)).thenReturn(List.of(o1, o2));

        StudentDetailsDto detailsDto = new StudentDetailsDto();
        detailsDto.setId(studentId);
        when(studentMapper.toStudentDetailsDto(student)).thenReturn(detailsDto);

        OrderDto orderDto1 = new OrderDto();
        orderDto1.setId("o1");
        OrderDto orderDto2 = new OrderDto();
        orderDto2.setId("o2");
        when(orderMapper.toOrderDto(o1)).thenReturn(orderDto1);
        when(orderMapper.toOrderDto(o2)).thenReturn(orderDto2);

        StudentDetailsDto result = studentService.getStudentDetails(studentId);

        assertEquals(studentId, result.getId());
        assertThat(result.getOrders()).hasSize(2);
        assertThat(result.getOrders()).extracting(OrderDto::getId).containsExactlyInAnyOrder("o1", "o2");
        verify(studentRepository).findById(studentId);
        verify(orderRepository).findOrdersWithSubscriptionByStudentId(studentId);
        verify(studentMapper).toStudentDetailsDto(student);
        verify(orderMapper).toOrderDto(o1);
        verify(orderMapper).toOrderDto(o2);
    }

    @Test
    @DisplayName("getStudentDetails throws when student not found")
    void getStudentDetails_notFound() {
        when(studentRepository.findById("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.getStudentDetails("missing"));
        assertThat(ex.getMessage()).isEqualTo("Student not found");
        verify(studentRepository).findById("missing");
        verifyNoInteractions(orderRepository, studentMapper, orderMapper);
    }
}
