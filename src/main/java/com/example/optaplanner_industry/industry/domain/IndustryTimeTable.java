package com.example.optaplanner_industry.industry.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@Data
@NoArgsConstructor
@PlanningSolution
public class IndustryTimeTable {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "processTimeslotRange")
    private List<ProcessTimeslot> processTimeslotList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "workerRange")
    private List<Resource> workerList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    private List<Resource> machineList;

    @PlanningEntityCollectionProperty
    private List<Task> taskList;

    @PlanningScore
    private HardSoftScore score;

    public IndustryTimeTable(List<ProcessTimeslot> processTimeslotList, List<Resource> workerList, List<Resource> machineList, List<Task> taskList) {
        this.processTimeslotList = processTimeslotList;
        this.workerList = workerList;
        this.machineList = machineList;
        this.taskList = taskList;
    }
}
