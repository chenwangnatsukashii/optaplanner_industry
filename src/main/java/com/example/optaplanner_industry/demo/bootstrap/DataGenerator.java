package com.example.optaplanner_industry.demo.bootstrap;

import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.LoadFile;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
//        ManufacturerOrder order = manufacturerOrderList.get(0);
        for(ManufacturerOrder order:manufacturerOrderList){
        Product product = order.getProduct();
        List<Step> stepList = product.getStepList();
        for (int i = stepList.size()-1; i >=0; i--) {
            Step step = stepList.get(i);
            List<ResourceRequirement> resourceRequirementList = step.getResourceRequirementList();
            List<Task> stepTaskList = step.getTaskList();
            Collections.reverse(stepTaskList);
            int number = i;
           for(int j  = stepTaskList.size()-1;j>=0;j--){
               Task item = stepTaskList.get(j);
//               if(j==stepTaskList.size()-1){
//                   item.setTaskType(TaskType.SINK);
//               }else if (j==0){
//                   item.setTaskType(TaskType.SOURCE);
//               }else {
//                   item.setTaskType(TaskType.STANDARD);
//
//               }
               item.setProduct(product);
               item.setProductId(product.getId());
               item.setStepId(step.getId());
               item.setStepIndex(number);
               item.setManufacturerOrder(order);
               item.setRequiredResourceId(resourceRequirementList.get(0).getResourceId());
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
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                if (i == 0) {
                    task.setTaskType(TaskType.SOURCE);
                } else if (i == taskList.size() - 1) {
                    task.setTaskType(TaskType.SINK);
                } else {
                    task.setTaskType(TaskType.STANDARD);
                }
            }
            order.setTaskList(taskList);
        }

        Collections.reverse(taskList);
//        taskList.forEach(i->{
//
//            System.out.println(i.getId()+" "+i.getStepIndex());
//            if(i.getNextTask()!=null){
//                System.out.println("next is:"+i.getNextTask().getId());
//            }
//        });

        return taskList;
    }

    public static List<Timeslot> generateTimeSlotList() {
        List<Timeslot> timeslotList = new ArrayList<>(3);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(12, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(17, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(18, 30), LocalTime.of(22, 30)));
        return timeslotList;
    }

    public static void sortTask(List<Task> taskList) {
        for (Task task : taskList) {

            Task successorTask = task.getNextTask();
            while (successorTask != null) {
                task.getSuccessorTaskList().add(successorTask);
                successorTask = successorTask.getNextTask();
            }
        }
//        for (Task task : taskList) {
//            System.out.println(task.getId());
//
//            List<Task> successorTaskList = task.getSuccessorTaskList();
//            for (Task task1 : successorTaskList) {
//                System.out.println("next task"+task1.getId());
//
//            }
//        }
    }

    public static List<Allocation> createAllocationList(List<Task> taskList,List<ManufacturerOrder> orderList){
        Integer orderListSize = orderList.size();
//        List<Task> taskList = generateTaskList();
        List<Allocation> allocationList = new ArrayList<>(taskList.size());
        Map<String, Allocation> taskToAllocationMap = new HashMap<>(taskList.size());
        Map<ManufacturerOrder, Allocation> orderToSourceAllocationMap = new HashMap<>(orderListSize);
        Map<ManufacturerOrder, Allocation> orderToSinkAllocationMap = new HashMap<>(orderListSize);
        for (Task task : taskList) {
            Allocation allocation = new Allocation();
            allocation.setId(task.getId());
            allocation.setTask(task);
            allocation.setPredecessorAllocationList(new ArrayList<>(task.getSuccessorTaskList().size()));
            allocation.setSuccessorAllocationList(new ArrayList<>(task.getSuccessorTaskList().size()));
            // Uninitialized allocations take no time, but don't break the predecessorsDoneDate cascade to sink.
            allocation.setPredecessorsDoneDate(task.getManufacturerOrder().getReleaseDate());
            if (task.getTaskType() == TaskType.SOURCE) {
                allocation.setDelay(0);
//                if (job.getExecutionModeList().size() != 1) {
//                    throw new IllegalArgumentException("The job (" + job
//                            + ")'s executionModeList (" + job.getExecutionModeList()
//                            + ") is expected to be a singleton.");
//                }
//                allocation.setExecutionMode(job.getExecutionModeList().get(0));
                orderToSourceAllocationMap.put(task.getManufacturerOrder(), allocation);
            } else if (task.getTaskType() == TaskType.SINK) {
                allocation.setDelay(0);
//                if (job.getExecutionModeList().size() != 1) {
//                    throw new IllegalArgumentException("The job (" + job
//                            + ")'s executionModeList (" + job.getExecutionModeList()
//                            + ") is expected to be a singleton.");
//                }
//                allocation.setExecutionMode(job.getExecutionModeList().get(0));
                orderToSinkAllocationMap.put(task.getManufacturerOrder(), allocation);
            }
            allocationList.add(allocation);
            taskToAllocationMap.put(task.getId(), allocation);
        }
        for (Task task : taskList) {
            if(task.getTaskType().equals(TaskType.SOURCE)){
                System.out.println(task.getId());
            }
        }
//        System.out.println(taskList.size());
//        System.out.println(allocationList.size());
//        System.out.println(taskToAllocationMap.size());
//
//        System.out.println(taskToAllocationMap.values().size());
        for(Allocation allocation: taskToAllocationMap.values()){
            System.out.println("task:"+allocation.getTask().getId()+"allocation:"+allocation.getId());
        }
        for (Allocation allocation : allocationList) {
            Task task = allocation.getTask();
            Allocation allocation1 = orderToSourceAllocationMap.get(task.getManufacturerOrder());
//            System.out.println("source:"+allocation1.getId());

            allocation.setSourceAllocation(orderToSourceAllocationMap.get(task.getManufacturerOrder()));
            Allocation allocation2 = orderToSinkAllocationMap.get(task.getManufacturerOrder());
//            System.out.println("sink:"+allocation2.getId());
            allocation.setSinkAllocation(orderToSinkAllocationMap.get(task.getManufacturerOrder()));
            for (Task successorJob : task.getSuccessorTaskList()) {
                System.out.println("currenttask："+task.getId());
                Allocation successorAllocation = taskToAllocationMap.get(successorJob.getId());
                System.out.println("nextlocation："+successorAllocation.getId() +"对应的task:"+successorAllocation.getTask().getId());

                allocation.getSuccessorAllocationList().add(successorAllocation);
                successorAllocation.getPredecessorAllocationList().add(allocation);
            }
        }
        for (Allocation sourceAllocation : orderToSourceAllocationMap.values()) {
            for (Allocation allocation : sourceAllocation.getSuccessorAllocationList()) {
                allocation.setPredecessorsDoneDate(sourceAllocation.getEndDate(150));
            }
        }
        return allocationList;
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
        List<ManufacturerOrder> manufacturerOrders = DataGenerator.generateOrderList();
        List<Task> taskList = DataGenerator.generateTaskList();
        sortTask(taskList);
        List<Allocation> allocationList = createAllocationList(taskList, manufacturerOrders);
//        allocationList.forEach(
//                i->{
//                    System.out.println(i.getId());
//                    List<Allocation> successorAllocationList = i.getSuccessorAllocationList();
//                    successorAllocationList.forEach(j->{
//                        System.out.println("successor:"+j.getId());
//
//                    });
//                }
//        );


    }

    private void reverse(Task task){

    }

}
