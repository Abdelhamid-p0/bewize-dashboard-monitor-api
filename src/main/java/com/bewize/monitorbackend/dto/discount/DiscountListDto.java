package com.bewize.monitorbackend.dto.discount;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountListDto {
    private String id;
    private String code;
    private Integer percentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}