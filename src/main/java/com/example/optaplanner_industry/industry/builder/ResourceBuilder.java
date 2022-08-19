package com.example.optaplanner_industry.industry.builder;

import com.example.optaplanner_industry.industry.domain.Resource;
import com.example.optaplanner_industry.industry.domain.Worker;

import java.util.List;

public abstract class ResourceBuilder {

    public abstract ResourceBuilder addWorker(String workerName, Integer[] resourceType, Integer scheduleType);

    public abstract ResourceBuilder addMachine(String[] resourceCodeList, Integer machineNumber, Integer[] resourceType, Integer oneHourProduction);

    public abstract List<Resource> builderResource();
}
