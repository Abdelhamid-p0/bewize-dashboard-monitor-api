package com.bewize.monitorbackend.dto.student;

import com.bewize.monitorbackend.dto.LevelDto;
import com.bewize.monitorbackend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentListDto extends StudentDto {

    private String phone;
    private Gender gender;
    private LocalDate signupDate;
    private LevelDto level;

}
