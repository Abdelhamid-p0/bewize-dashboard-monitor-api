package com.bewize.monitorbackend.repository.projection;

public interface DashboardMonthCountProjection {
    String getLabel(); // returns "YYYY-MM"
    Long getCount();
}

