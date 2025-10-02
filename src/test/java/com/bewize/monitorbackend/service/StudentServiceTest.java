package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.dto.order.OrderDto;
import com.bewize.monitorbackend.dto.student.StudentDetailsDto;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.mappers.OrderMapper;
import com.bewize.monitorbackend.mappers.StudentMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("getStudents returns mapped list")
    void getStudents_ok() {
        Student s1 = new Student(); s1.setId("s1");
        Student s2 = new Student(); s2.setId("s2");
        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        when(studentMapper.toStudentListDto(any(Student.class))).thenAnswer(inv -> {
            Student src = inv.getArgument(0);
            StudentListDto dto = new StudentListDto();
            dto.setId(src.getId());
            return dto;
        });

        List<StudentListDto> result = studentService.getStudents();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(StudentListDto::getId)
                .containsExactlyInAnyOrder("s1", "s2");

        verify(studentRepository, times(1)).findAll();
        verify(studentMapper, times(2)).toStudentListDto(any(Student.class));
    }

    @Test
    @DisplayName("getStudentDetails returns details with mapped orders")
    void getStudentDetails_ok() {
        String studentId = "stu-123";
        Student student = new Student(); student.setId(studentId); student.setFirstName("Ana");
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Order o1 = new Order(); o1.setId("o1");
        Order o2 = new Order(); o2.setId("o2");
        when(orderRepository.findOrdersWithSubscriptionByStudentId(studentId)).thenReturn(List.of(o1, o2));

        StudentDetailsDto detailsDto = new StudentDetailsDto();
        detailsDto.setId(studentId);
        when(studentMapper.toStudentDetailsDto(student)).thenReturn(detailsDto);

        OrderDto orderDto1 = new OrderDto(); orderDto1.setId("o1");
        OrderDto orderDto2 = new OrderDto(); orderDto2.setId("o2");
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
