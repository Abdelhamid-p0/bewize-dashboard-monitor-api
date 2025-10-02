package com.bewize.monitorbackend.repository.projection;

import com.bewize.monitorbackend.enums.PlanType;

import java.time.LocalDateTime;
import java.util.Date;

public interface SubscriptionListProjection {
    String getId();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();

    OrderProjection getOrder();

    interface OrderProjection {
        String getId();
    }
}
