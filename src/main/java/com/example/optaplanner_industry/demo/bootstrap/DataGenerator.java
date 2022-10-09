package com.example.optaplanner_industry.demo.bootstrap;

import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.LoadFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataGenerator {
    protected final static String FILE_PATH = "json/input_2.json";
    public final static String OUTPUT_PATH = "output.json";
    private final static Input input = LoadFile.readJsonFile(FILE_PATH);

    public static Input getInput() {
        return input;
    }

    public static void writeObjectToFile(Object output) {
        LoadFile.writeJsonFile(output, OUTPUT_PATH);
    }

    public static List<ResourceItem> generateResources() {
        List<ResourceItem> resourceItemList = new ArrayList<>();
        List<ResourcePool> resourcePool = input.getResourcePool();
        resourcePool.forEach(each -> {
            ResourceItem available = each.getAvailableList().get(0);
            available.setResourcePoolId(each.getId());
            resourceItemList.add(available);

        });
        return resourceItemList;
    }


    public static List<Task> generateTaskList() {
        List<ManufacturerOrder> manufacturerOrderList = input.getManufacturerOrderList();
        List<Task> taskList = new ArrayList<>();
        for (ManufacturerOrder order : manufacturerOrderList) {
            Product product = order.getProduct();
            List<Step> stepList = product.getStepList();
            for (int i = stepList.size() - 1; i >= 0; i--) {
                Step step = stepList.get(i);
                List<Task> stepTaskList = step.getTaskList();
//            Collections.reverse(stepTaskList);
                for (int j = stepTaskList.size() - 1; j >= 0; j--) {
                    Task item = stepTaskList.get(j);
                    item.setId(order.getCode() + '_' + item.getId());
                    item.setProductId(product.getId());
                    item.setStepId(step.getId());
                    item.setStepIndex(i);
                    item.setRequiredResourceId(step.getResourceRequirementList().get(0).getResourceId());
                    item.setManufactureOrderId(order.getId());
                    item.setQuantity(order.getQuantity());
                    item.setTaskBeginTime(order.getPeriod().getStartTime());
                    if (i < stepList.size() - 1) {
                        Task one = taskList.get(taskList.size() - stepTaskList.size() + j);
                        item.setNextTask(one);
                        one.setPreTask(item);
                    }
                }

                taskList.addAll(stepTaskList);
            }
        }

        Collections.reverse(taskList);
        return taskList;
    }


    public static List<ScheduleDate> generateScheduleDateList() {
        List<ScheduleDate> scheduleDateList = new ArrayList<>(14);
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now(), null));

        for (int i = 1; i < 14; i++) {
            scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(i), null));
        }

        return scheduleDateList;
    }


}
