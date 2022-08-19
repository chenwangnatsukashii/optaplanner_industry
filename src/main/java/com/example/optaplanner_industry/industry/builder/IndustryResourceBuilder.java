package com.example.optaplanner_industry.industry.builder;

import com.example.optaplanner_industry.industry.domain.Machine;
import com.example.optaplanner_industry.industry.domain.Resource;
import com.example.optaplanner_industry.industry.domain.Worker;

import java.util.ArrayList;
import java.util.List;

public class IndustryResourceBuilder extends ResourceBuilder {

    private final List<Resource> resourceList = new ArrayList<>(128);
    private long resourceId = 1;

    @Override
    public ResourceBuilder addWorker(String workerName, Integer[] resourceType, Integer scheduleType) {
        resourceList.add(new Worker(resourceId++, workerName, resourceType, scheduleType));
        return this;
    }

    @Override
    public ResourceBuilder addMachine(String[] resourceCodeList, Integer machineNumber, Integer[] machineType, Integer oneHourProduction) {
        for (int i = 0; i <= machineNumber; i++) {
            resourceList.add(new Machine(resourceId++, resourceCodeList[i], machineType, oneHourProduction));
        }
        return this;
    }

    @Override
    public List<Resource> builderResource() {
        return resourceList;
    }
}
