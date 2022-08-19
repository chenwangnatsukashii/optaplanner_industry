package com.example.optaplanner_industry.industry.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Machine extends Resource {

    private Integer oneHourProduction;

    public Machine(long machineId, String resourceCode, Integer[] machineType, Integer oneHourProduction) {
        this.resourceId = machineId;
        this.resourceCode = resourceCode;
        this.resourceType = machineType;
        this.oneHourProduction = oneHourProduction;

    }

}
