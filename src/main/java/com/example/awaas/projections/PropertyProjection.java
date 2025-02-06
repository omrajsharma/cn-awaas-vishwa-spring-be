package com.example.awaas.projections;

import com.example.awaas.enums.PropertyTypeEnum;

public interface PropertyProjection {
    Long getId();

    String getTitle();

    Double getPrice();

    String getLocation();

    PropertyTypeEnum getType();
}
