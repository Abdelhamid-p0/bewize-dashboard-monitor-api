package com.bewize.monitorbackend.dto.student;

import com.bewize.monitorbackend.dto.LevelDto;
import com.bewize.monitorbackend.dto.order.OrderDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class StudentDetailsDto extends StudentDto {

    private String phone;
    private String gender;
    private String birthday;
    private Date lastLogin;

    private LevelDto level;

    private boolean active;
    private boolean deleted;
    private Integer finishedCoursesCount;
    private String locationCountry;
    private String locationCity;
    private Date lastVisit;
    private String singupDate;
    private List<OrderDto> orders;

}
