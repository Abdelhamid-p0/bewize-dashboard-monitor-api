package com.bewize.monitorbackend.dto;

import com.bewize.monitorbackend.enums.Cycle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelDto {

    private String levelName;
    private Cycle cycle;
}
