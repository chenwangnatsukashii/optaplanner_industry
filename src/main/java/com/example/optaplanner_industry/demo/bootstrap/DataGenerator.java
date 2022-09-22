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
    LoadFile loadFile = new LoadFile();

    public static List<ResourceItem> generateResources(){
        List<ResourceItem> resourceItemList = new ArrayList<>(9);
        resourceItemList.add(new ResourceItem("resource_0","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_1","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_2","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_3","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_4","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_5","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_6","dakongshebei01",
                null,1));       resourceItemList.add(new ResourceItem("resource_0","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_7","dakongshebei01",
                null,1));
        resourceItemList.add(new ResourceItem("resource_8","dakongshebei01",
                null,1));
        return resourceItemList;
    }

    public static List<ManufacturerOrder> generateOrderList(){
        Input input = LoadFile.readJsonFile(FILE_PATH);
        return input.getManufacturerOrderList();
    }

    public static List<Task> generateTaskList(){
        Input input = LoadFile.readJsonFile(FILE_PATH);
        List<ManufacturerOrder> manufacturerOrderList = input.getManufacturerOrderList();
        List<Task> taskList = new ArrayList<>();
        for(ManufacturerOrder order:manufacturerOrderList){
            Product product = order.getProduct();
            List<Step> stepList = product.getStepList();
            for(Step step:stepList){
                List<Task> stepTaskList = step.getTaskList();
                stepTaskList.forEach(i->{
                    i.setProductId(product.getId());
                    i.setStepId(step.getId());
                });
                taskList.addAll(stepTaskList);
            }
        }
        return taskList;
    }

    public static List<Timeslot> generateTimeSlotList(){
        List<Timeslot> timeslotList = new ArrayList<>(3);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(12, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(17, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(18, 30), LocalTime.of(22, 30)));
        return timeslotList;
    }


    public static List<ScheduleDate> generateScheduleDateList(){
        List<ScheduleDate> scheduleDateList = new ArrayList<>(14);
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now(),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(1),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(2),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(3),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(4),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(5),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(6),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(7),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(8),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(9),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(10),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(11),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(12),null));
        scheduleDateList.add(new ScheduleDate(LocalDateTime.now().plusDays(13),null));
        return scheduleDateList;
    }

    public static void main(String[] args) {
        List<ManufacturerOrder> scheduleDates = generateOrderList();
        System.out.println(scheduleDates.size());
        scheduleDates.forEach(i->System.out.println(i.toString()));
    }

}
