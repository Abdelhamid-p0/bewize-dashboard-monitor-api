package com.bewize.monitorbackend.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerPeriodResponseDto {
    // If request was year only -> months will contain 12 entries ("YYYY-01".."YYYY-12")
    // If request was year+month -> months contains days ("YYYY-MM-DD") or label uses day numbers
    private List<TimePointDto> points;
    private long total;
}

