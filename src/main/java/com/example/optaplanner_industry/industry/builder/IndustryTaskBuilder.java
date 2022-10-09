package com.example.optaplanner_industry.industry.builder;


import com.example.optaplanner_industry.demo.domain.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IndustryTaskBuilder extends TaskBuilder {

    private final List<Task> taskList = new ArrayList<>(128);

    @Override
    public List<Task> builderTask() {
        return taskList;
    }
}
