package com.example.optaplanner_industry.example.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScoreHolder;

import java.util.List;

@Data
@NoArgsConstructor
@PlanningSolution
public class TaskAssignment {
    HardSoftScoreHolder ds;

    @PlanningScore
    private HardSoftScore score;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "workGroupRange")
    private List<WorkGroup> workGroupList;
    @PlanningEntityCollectionProperty
    private List<Task> taskList;
    public TaskAssignment(List<WorkGroup> workGroupList, List<Task> taskList) {
        //super(0);
        this.workGroupList = workGroupList;
        this.taskList = taskList;
    }

}
