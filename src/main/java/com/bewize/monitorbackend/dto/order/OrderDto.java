package com.bewize.monitorbackend.dto.order;

import com.bewize.monitorbackend.dto.subscription.SubscriptionDto;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.OrderType;
import com.bewize.monitorbackend.enums.PlanType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDto {
    private String id;
    private String code;
    private OrderType type;
    private OrderStatus status;
    private PlanType planType;
    private LocalDateTime date;
    private Float amount;
    private SubscriptionDto subscription;
}
