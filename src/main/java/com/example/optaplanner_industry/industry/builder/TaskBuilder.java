package com.example.optaplanner_industry.industry.builder;


import com.example.optaplanner_industry.demo.domain.Task;

import java.time.LocalDate;
import java.util.List;

public abstract class TaskBuilder {

    public abstract TaskBuilder addTask(String taskName, Integer quantity, Integer layerNumber, LocalDate startTime, LocalDate endTime, Integer priority);

    public abstract List<Task> builderTask();

}
