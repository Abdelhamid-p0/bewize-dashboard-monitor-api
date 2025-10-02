package com.bewize.monitorbackend.mappers;

import com.bewize.monitorbackend.dto.Dashboard.TimePointDto;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    TimePointDto monthProjectionToDto(DashboardMonthCountProjection p);
    List<TimePointDto> monthProjectionListToDtoList(List<DashboardMonthCountProjection> list);

    TimePointDto dayProjectionToDto(DashboardDayCountProjection p);
    List<TimePointDto> dayProjectionListToDtoList(List<DashboardDayCountProjection> list);
}
