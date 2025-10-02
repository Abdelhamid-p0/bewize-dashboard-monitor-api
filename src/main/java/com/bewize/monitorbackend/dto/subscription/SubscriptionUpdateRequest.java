package com.bewize.monitorbackend.dto.subscription;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionUpdateRequest {
    private LocalDateTime startDate; // optional
    private LocalDateTime endDate;   // optional
    private String orderId;          // optional
}