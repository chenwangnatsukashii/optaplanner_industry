package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
public class Task {

    @PlanningId
    private Long id;

    private String subject;
    private String taskOrder;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    private Timeslot timeslot;
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private WorkGroup workGroup;

    public Task(long id, String subject, String taskOrder) {
        this.id = id;
        this.subject = subject;
        this.taskOrder = taskOrder;
    }

    @Override
    public String toString() {
        return subject + "(" + id + ")";
    }

}
