package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.dto.LevelDto;
import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.dto.student.StudentDetailsDto;
import com.bewize.monitorbackend.mappers.OrderMapper;
import com.bewize.monitorbackend.mappers.StudentMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.projection.StudentListProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final OrderRepository orderRepository;

    private final StudentMapper studentMapper;
    private final OrderMapper orderMapper;


    @Transactional(readOnly = true)
    public PageResponse<StudentListDto> getStudents(Pageable pageable) {

        Page<StudentListProjection> page = studentRepository.findAllProjectedBy(pageable);

        List<StudentListDto> data = page.getContent().stream().map(p -> {
            StudentListDto dto = new StudentListDto();
            dto.setId(p.getId());
            dto.setCne(p.getCne());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setPhone(p.getPhone());
            dto.setGender(p.getGender());
            dto.setSingupDate(p.getsingupDate());

            LevelDto level = new LevelDto();
            if (p.getLevel() != null) {
                level.setLevelName(p.getLevel().getLevelName());
                level.setCycle(p.getLevel().getCycle());
            }
            dto.setLevel(level);

            return dto;
        }).toList();

        PageResponse.Meta meta = new PageResponse.Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new PageResponse<>(data, meta);
    }


    @Transactional(readOnly = true)
    public StudentDetailsDto getStudentDetails(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Order> orders = orderRepository.findOrdersWithSubscriptionByStudentId(studentId);

        StudentDetailsDto dto = studentMapper.toStudentDetailsDto(student);
        dto.setOrders(orders.stream().map(orderMapper::toOrderDto).collect(Collectors.toList()));
        return dto;
    }

}
