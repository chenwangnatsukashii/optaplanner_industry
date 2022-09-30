package com.example.optaplanner_industry.demo.domain;


import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;


import java.util.List;

@PlanningSolution
public class Schedule{

    private List<ManufacturerOrder> manufacturerOrderList;

    private List<Task> taskList;
    private List<ResourceItem> resourceList;
//    private List<ResourceRequirement> resourceRequirementList;

    private List<Allocation> allocationList;

    private HardSoftScore score;

    @ProblemFactCollectionProperty
    public List<ManufacturerOrder> getManufacturerOrderList() {
        return manufacturerOrderList;
    }

    public void setManufacturerOrderList(List<ManufacturerOrder> manufacturerOrderList) {
        this.manufacturerOrderList = manufacturerOrderList;
    }

    @ProblemFactCollectionProperty
    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }


    @ProblemFactCollectionProperty
    public List<ResourceItem> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceItem> resourceList) {
        this.resourceList = resourceList;
    }

//    @ProblemFactCollectionProperty
//    public List<ResourceRequirement> getResourceRequirementList() {
//        return resourceRequirementList;
//    }
//
//    public void setResourceRequirementList(List<ResourceRequirement> resourceRequirementList) {
//        this.resourceRequirementList = resourceRequirementList;
//    }

    @PlanningEntityCollectionProperty
    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<Allocation> allocationList) {
        this.allocationList = allocationList;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
