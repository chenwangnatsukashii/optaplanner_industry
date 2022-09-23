package com.example.optaplanner_industry.demo.bootstrap;

import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.LoadFile;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataGenerator {
    static String FILE_PATH = "json/input_2.json";
    static Input input;

    static {
        input = LoadFile.readJsonFile(FILE_PATH);
    }


    public static List<ResourceItem> generateResources() {
        List<ResourceItem> resourceItemList = new ArrayList<>();
        List<ResourcePool> resourcePool = input.getResourcePool();
        resourcePool.forEach(each -> {
            List<ResourceItem> available = each.getAvailableList();
            resourceItemList.add(available.get(0));

        });
        return resourceItemList;
    }

    public static List<ManufacturerOrder> generateOrderList() {
        Input input = LoadFile.readJsonFile(FILE_PATH);
        return input.getManufacturerOrderList();
    }

    public static List<Task> generateTaskList() {
        List<ManufacturerOrder> manufacturerOrderList = input.getManufacturerOrderList();
        List<Task> taskList = new ArrayList<>();
        ManufacturerOrder order = manufacturerOrderList.get(0);
//        for(ManufacturerOrder order:manufacturerOrderList){
        Product product = order.getProduct();
        List<Step> stepList = product.getStepList();
        for (int i = 0; i < stepList.size(); i++) {
            Step step = stepList.get(i);
            List<Task> stepTaskList = step.getTaskList();
            Integer number = i;
            stepTaskList.forEach(item -> {
                item.setProductId(product.getId());
                item.setStepId(step.getId());
                item.setStepIndex(number);
                item.setRequiredResourceId(step.getResourceRequirementList().get(0).getResourceId());
            });
            taskList.addAll(stepTaskList);
        }
//        }
        return taskList;
    }

    public static List<Timeslot> generateTimeSlotList() {
        List<Timeslot> timeslotList = new ArrayList<>(3);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(12, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(17, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(18, 30), LocalTime.of(22, 30)));
        return timeslotList;
    }


    public static List<ScheduleDate> generateScheduleDateList() {
        List<ScheduleDate> scheduleDateList = new ArrayList<>(14);
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now(), null));

        for (int i = 1; i < 14; i++) {
            scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(i), null));
        }

        return scheduleDateList;
    }

    public static void main(String[] args) {
        List<Task> taskList = generateTaskList();
        for (Task i : taskList) {
            System.out.println(i.toString());
        }

    }

}
