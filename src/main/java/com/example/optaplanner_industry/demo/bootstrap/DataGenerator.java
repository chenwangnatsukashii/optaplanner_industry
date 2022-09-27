package com.example.optaplanner_industry.demo.bootstrap;

import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.LoadFile;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            ResourceItem available = each.getAvailableList().get(0);
            available.setResourcePoolId(each.getId());
            resourceItemList.add(available);

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
        for (int i = stepList.size()-1; i >=0; i--) {
            Step step = stepList.get(i);
            List<Task> stepTaskList = step.getTaskList();
//            Collections.reverse(stepTaskList);
            int number = i;
           for(int j  = stepTaskList.size()-1;j>=0;j--){
               Task item = stepTaskList.get(j);
               item.setProductId(product.getId());
               item.setStepId(step.getId());
               item.setStepIndex(number);
               item.setRequiredResourceId(step.getResourceRequirementList().get(0).getResourceId());
               if (number < stepList.size()-1) {
                   item.setNextTask(taskList.get(taskList.size() - stepTaskList.size()+j));
               }
           }
//            stepTaskList.forEach(item -> {
//                item.setProductId(product.getId());
//                item.setStepId(step.getId());
//                item.setStepIndex(number);
//                item.setRequiredResourceId(step.getResourceRequirementList().get(0).getResourceId());
//                if (number > 0)
//                    item.setNextTask(taskList.get(taskList.size() - stepTaskList.size()+j));
//            });

            taskList.addAll(stepTaskList);
        }
//        }

//        Collections.reverse(taskList);
        taskList.forEach(i->{

            System.out.println(i.getId()+" "+i.getStepIndex());
            if(i.getNextTask()!=null){
                System.out.println("next is:"+i.getNextTask().getId());
            }
        });
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


    }

    private void reverse(Task task){

    }

}
