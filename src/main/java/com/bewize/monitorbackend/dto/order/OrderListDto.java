package com.bewize.monitorbackend.dto.order;

import com.bewize.monitorbackend.dto.discount.DiscountDto;
import com.bewize.monitorbackend.dto.student.StudentDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderListDto extends OrderDto {
    private StudentDto student;
    private DiscountDto discount;

}
