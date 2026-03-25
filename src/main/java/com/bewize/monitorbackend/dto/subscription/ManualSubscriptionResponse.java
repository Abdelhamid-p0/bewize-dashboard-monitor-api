package com.bewize.monitorbackend.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ManualSubscriptionResponse {
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String orderId;
}
