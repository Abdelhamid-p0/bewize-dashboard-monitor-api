package com.bewize.monitorbackend.dto.discount;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountUpdateRequest {
    private String code;            // optional
    private Integer percentage;     // optional
    private LocalDateTime startDate; // optional
    private LocalDateTime endDate;   // optional
}