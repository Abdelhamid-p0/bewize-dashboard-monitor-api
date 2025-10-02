package com.bewize.monitorbackend.repository.projection;

import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.OrderType;
import com.bewize.monitorbackend.enums.PlanType;

import java.time.LocalDateTime;

public interface OrderListProjection {
    String getId();
    String getCode();
    OrderType getType();
    OrderStatus getStatus();
    PlanType getPlanType();
    LocalDateTime getDate();
    Float getAmount();
    String getTransactionId();

    SubscriptionProjection getSubscription();

    StudentProjection getStudent();

    DiscountProjection getDiscount();

    interface SubscriptionProjection {
        String getId();
        LocalDateTime getStartDate();
        LocalDateTime getEndDate();
    }

    interface StudentProjection {
        String getId();
        String getFirstName();
        String getLastName();
        String getEmail();
    }

    interface DiscountProjection {
        String getId();
        String getCode();
        Float getPercentage();
    }
}
