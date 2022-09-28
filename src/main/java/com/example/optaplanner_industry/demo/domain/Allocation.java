package com.example.optaplanner_industry.demo.domain;

import com.example.optaplanner_industry.demo.domain.solver.DelayStrengthComparator;
import com.example.optaplanner_industry.demo.domain.solver.NotSourceOrSinkAllocationFilter;
import com.example.optaplanner_industry.demo.domain.solver.PredecessorsDoneDateUpdatingVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;


import java.util.List;

@PlanningEntity(pinningFilter = NotSourceOrSinkAllocationFilter.class)
public class Allocation{

    private String id;

    @PlanningVariable(valueRangeProviderRefs = "taskRange")
    private Task task;

    private Allocation sourceAllocation;
    private Allocation sinkAllocation;
    private List<Allocation> predecessorAllocationList;
    private List<Allocation> successorAllocationList;

    // Planning variables: changes during planning, between score calculations.
//    private ExecutionMode executionMode;
    private Integer delay; // In days

    // Shadow variables
    private Integer predecessorsDoneDate;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Allocation getSourceAllocation() {
        return sourceAllocation;
    }

    public void setSourceAllocation(Allocation sourceAllocation) {
        this.sourceAllocation = sourceAllocation;
    }

    public Allocation getSinkAllocation() {
        return sinkAllocation;
    }

    public void setSinkAllocation(Allocation sinkAllocation) {
        this.sinkAllocation = sinkAllocation;
    }

    public List<Allocation> getPredecessorAllocationList() {
        return predecessorAllocationList;
    }

    public void setPredecessorAllocationList(List<Allocation> predecessorAllocationList) {
        this.predecessorAllocationList = predecessorAllocationList;
    }

    public List<Allocation> getSuccessorAllocationList() {
        return successorAllocationList;
    }

    public void setSuccessorAllocationList(List<Allocation> successorAllocationList) {
        this.successorAllocationList = successorAllocationList;
    }

//    @PlanningVariable(valueRangeProviderRefs = {
//            "executionModeRange" }, strengthWeightFactoryClass = ExecutionModeStrengthWeightFactory.class)
//    public ExecutionMode getExecutionMode() {
//        return executionMode;
//    }
//
//    public void setExecutionMode(ExecutionMode executionMode) {
//        this.executionMode = executionMode;
//    }

    @PlanningVariable(valueRangeProviderRefs = { "delayRange" }, strengthComparatorClass = DelayStrengthComparator.class)
    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @CustomShadowVariable(variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "task"),
            @PlanningVariableReference(variableName = "delay") })
    public Integer getPredecessorsDoneDate() {
        return predecessorsDoneDate;
    }

    public void setPredecessorsDoneDate(Integer predecessorsDoneDate) {
        this.predecessorsDoneDate = predecessorsDoneDate;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Integer getStartDate() {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay);
    }

    public Integer getEndDate(int amount) {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay) +(int) Math.ceil((double)amount / this.getTask().getSpeed());
    }

    public ManufacturerOrder getManufacturerOrder() {
        return task.getManufacturerOrder();
    }

//    public int getProjectCriticalPathEndDate() {
//        return job.getProject().getCriticalPathEndDate();
//    }

//    public JobType getJobType() {
//        return job.getJobType();
//    }

    public String getLabel() {
        return "Job " + task.getId();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

//    @ValueRangeProvider(id = "executionModeRange")
//    public List<ExecutionMode> getExecutionModeRange() {
//        return job.getExecutionModeList();
//    }
//
    @ValueRangeProvider(id = "delayRange")
    public CountableValueRange<Integer> getDelayRange() {
        return ValueRangeFactory.createIntValueRange(0, 10);
    }

}
