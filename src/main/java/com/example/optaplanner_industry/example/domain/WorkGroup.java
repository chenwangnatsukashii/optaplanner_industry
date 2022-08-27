package com.example.optaplanner_industry.example.domain;

import lombok.Data;

@Data
public class WorkGroup {
    private Integer id;
    private String yarnType;
    private int capacity;
    private int cost;
    public WorkGroup(int id, String yarnType, int capacity, int cost) {
        this.id =id;
        this.yarnType = yarnType;
        this.capacity = capacity;
        this.cost = cost;
    }
}
