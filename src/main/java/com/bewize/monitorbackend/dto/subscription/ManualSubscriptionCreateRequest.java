package com.bewize.monitorbackend.dto.subscription;

import com.bewize.monitorbackend.enums.PlanType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ManualSubscriptionCreateRequest {
    private String studentId;
    private PlanType planType;
    private LocalDateTime startDate;
}
