package com.bewize.monitorbackend.mappers;


import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.dto.student.StudentDetailsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentListDto toStudentListDto(Student student);

    StudentDetailsDto toStudentDetailsDto(Student student);
}
