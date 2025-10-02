package com.bewize.monitorbackend.mappers;

import com.bewize.monitorbackend.domains.subscription.Discount;
import com.bewize.monitorbackend.dto.discount.DiscountCreateRequest;
import com.bewize.monitorbackend.dto.discount.DiscountListDto;
import com.bewize.monitorbackend.dto.discount.DiscountUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountListDto toListDto(Discount discount);

    @Mapping(target = "id", ignore = true)
    Discount toEntity(DiscountCreateRequest req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromUpdateRequest(DiscountUpdateRequest req, @MappingTarget Discount entity);
}