package com.bewize.monitorbackend.dto.discount;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountDto {
    private String id;
    private Float percentage;
}
