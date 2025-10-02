package com.bewize.monitorbackend.dto.subscription;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionListDto {
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String orderId;
}