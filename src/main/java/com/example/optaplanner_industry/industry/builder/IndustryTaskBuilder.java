package com.example.optaplanner_industry.industry.builder;


import com.example.optaplanner_industry.demo.domain.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IndustryTaskBuilder extends TaskBuilder {

    private final List<Task> taskList = new ArrayList<>(128);
    private long taskId = 1;

    @Override
    public TaskBuilder addTask(String taskName, Integer quantity, Integer layerNumber, LocalDate startTime, LocalDate endTime, Integer priority) {

        int maxTaskOrder = 4;

        for (int i = 1; i <= layerNumber; i++) {
            for (int taskOrder = 1; taskOrder <= maxTaskOrder; taskOrder++) {
                taskList.add(new Task(taskId++, taskName, quantity, 0, "工作组0" + taskOrder, i, new ArrayList<>()));
            }
        }

        return this;
    }

    @Override
    public List<Task> builderTask() {
        return taskList;
    }
}
