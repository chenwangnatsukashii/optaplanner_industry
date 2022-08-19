package com.example.optaplanner_industry.industry.domain;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Worker extends Resource {

    private Integer scheduleType;

    public Worker(long workerId, String resourceCode, Integer[] workerType, Integer scheduleType) {
        this.resourceId = workerId;
        this.resourceCode = resourceCode;
        this.resourceType = workerType;
        this.scheduleType = scheduleType;
    }

}
