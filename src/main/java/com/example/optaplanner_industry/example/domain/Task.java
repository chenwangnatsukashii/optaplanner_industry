package com.example.optaplanner_industry.example.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
@Data
public class Task {
    @PlanningId
    private Integer id;
    private String requiredYarnType;
    private int amount;
    @PlanningVariable(valueRangeProviderRefs = "workGroupRange")
    private WorkGroup workGroup;


    public Task(){}

    public Task(int id, String requiredYarnType, int amount) {
        this.id= id;
        this.requiredYarnType = requiredYarnType;
        this.amount = amount;
    }
}
