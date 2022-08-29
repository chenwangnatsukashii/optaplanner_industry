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

    // 任务名称
    private String taskName;

    // 任务数量
    private Integer quantity;
    // 工序
    private String taskOrder;

    // 层
    private Integer layerNumber;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    private Timeslot timeslot;
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private WorkGroup workGroup;

    public Task(long id, String taskName, Integer quantity, String taskOrder, Integer layerNumber) {
        this.id = id;
        this.taskName = taskName;
        this.quantity = quantity;
        this.taskOrder = taskOrder;
        this.layerNumber = layerNumber;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", quantity=" + quantity +
                ", taskOrder='" + taskOrder + '\'' +
                ", layerNumber=" + layerNumber +
                ", timeslot=" + timeslot +
                ", workGroup=" + workGroup +
                '}';
    }
}
