package com.example.optaplanner_industry.industry.domain;


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
    private String taskName;
    private Integer quantity;
    private Integer taskOrder;
    private Integer layerNumber;

    @PlanningVariable(valueRangeProviderRefs = "processTimeslotRange")
    private ProcessTimeslot processTimeslot;

    @PlanningVariable(valueRangeProviderRefs = "workerRange")
    private Worker worker;

    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine machine;

    public Task(long id, String taskName, Integer quantity, Integer taskOrder, Integer layerNumber) {
        this.id = id;
        this.taskName = taskName;
        this.quantity = quantity;
        this.taskOrder = taskOrder;
        this.layerNumber = layerNumber;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", layerNumber=" + layerNumber +
                ", taskOrder=" + taskOrder +
                '}';
    }
}
