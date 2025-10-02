package com.bewize.monitorbackend.repository.projection;

import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;

import java.sql.Date;

public interface StudentListProjection {

    String getId();
    String getCne();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhone();
    Gender getGender();
    Date getsingupDate();

    LevelProjection getLevel();

    interface LevelProjection {
        String getLevelName();
        Cycle getCycle();
    }
}