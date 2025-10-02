package com.bewize.monitorbackend.dto.student;

import com.bewize.monitorbackend.dto.LevelDto;
import com.bewize.monitorbackend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentListDto extends StudentDto {

    private String phone;
    private Gender gender;
    private Date singupDate;
    private LevelDto level;

}
