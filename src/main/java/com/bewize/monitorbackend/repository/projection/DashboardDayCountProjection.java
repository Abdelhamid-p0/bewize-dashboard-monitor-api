package com.bewize.monitorbackend.repository.projection;

public interface DashboardDayCountProjection {
    String getLabel(); // returns "YYYY-MM-DD"
    Long getCount();
}
