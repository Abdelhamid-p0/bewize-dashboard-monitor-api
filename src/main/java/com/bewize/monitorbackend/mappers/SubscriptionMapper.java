package com.bewize.monitorbackend.mappers;

import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.dto.subscription.SubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "orderId", source = "order.id")
    SubscriptionListDto toListDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    Subscription toEntity(SubscriptionCreateRequest req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromUpdateRequest(SubscriptionUpdateRequest req, @MappingTarget Subscription entity);
}
