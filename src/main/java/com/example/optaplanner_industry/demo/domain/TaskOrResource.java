package com.example.optaplanner_industry.demo.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@Data
@PlanningEntity
public abstract class TaskOrResource {

    @InverseRelationShadowVariable(sourceVariableName = "previousTaskOrResource")
    protected Task nextTask;

    public abstract Integer getEndTime(int quantity);

}
