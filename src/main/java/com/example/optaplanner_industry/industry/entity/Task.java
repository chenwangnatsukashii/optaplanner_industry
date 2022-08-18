package com.example.optaplanner_industry.industry.entity;


import com.example.optaplanner_industry.domain.Timeslot;
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

    private String taskType;

    private Integer taskOrder;

    @PlanningVariable(valueRangeProviderRefs = "processTimeslotRange")
    private ProcessTimeslot processTimeslot;

    @PlanningVariable(valueRangeProviderRefs = "workerRange")
    private Worker worker;

    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine machine;
}
