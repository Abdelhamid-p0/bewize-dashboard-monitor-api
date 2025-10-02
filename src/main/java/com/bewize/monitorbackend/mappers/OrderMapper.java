package com.bewize.monitorbackend.mappers;


import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.dto.order.OrderDto;
import com.bewize.monitorbackend.dto.order.OrderListDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toOrderDto(Order order);

    OrderListDto toOrderListDto(Order order);
}
