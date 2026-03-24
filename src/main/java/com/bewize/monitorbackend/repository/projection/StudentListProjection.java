package com.bewize.monitorbackend.repository.projection;

import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface StudentListProjection {

    String getId();

    String getCne();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getPhone();

    Gender getGender();

    LocalDate getsignupDate();

    LevelProjection getLevel();

    interface LevelProjection {
        String getLevelName();

        Cycle getCycle();
    }
}