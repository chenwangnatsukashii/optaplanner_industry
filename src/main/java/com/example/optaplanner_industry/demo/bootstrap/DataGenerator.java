package com.example.optaplanner_industry.demo.bootstrap;

import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.LoadFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataGenerator {
    protected final static String FILE_PATH = "json/input_2.json";
    public final static String OUTPUT_PATH = "output.json";
    public final static String RESULT_PATH = "result.json";

    private final static Input input = LoadFile.readJsonFile(FILE_PATH);

    public static Input getInput() {
        return input;
    }

    public static void writeObjectToFile(Object output) {
        LoadFile.writeJsonFile(output, OUTPUT_PATH);
    }

    public static void writeResult(Object output) {
        LoadFile.writeJsonFile(output, RESULT_PATH);
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
            for (Step step : stepList) {
                List<ResourceRequirement> resourceRequirementList = step.getResourceRequirementList();
                List<Task> list = step.getTaskList();
                for (Task task : list) {
                    task.setProduct(product);
                    task.setProductId(product.getId());
                    task.setStepId(step.getId());
                    task.setOrderId(order.getId());
                    //duration 还得修改
                    task.setDuration((int) Math.ceil((double) order.getQuantity() / task.getSpeed()));
                    task.setSingleTimeSlotSpeed(BigDecimal.valueOf(task.getSpeed()).divide(BigDecimal.valueOf(3), 4, RoundingMode.CEILING));
                    task.setTimeSlotDuration(BigDecimal.valueOf(order.getQuantity()).divide(task.getSingleTimeSlotSpeed(), 4, RoundingMode.CEILING));
                    task.setMinutesDuration((int) Math.ceil(24.0 * 60 * order.getQuantity() / task.getSpeed()));
                    task.setManufacturerOrder(order);
                    task.setRequiredResourceId(resourceRequirementList.get(0).getResourceId());
                }
                taskList.addAll(list);
            }
        }
        //对每个unit = 0的单个任务设置
        Map<String, Map<Integer, List<Task>>> orderIdToLayerNumberToTasks =
                taskList.parallelStream().filter(task -> task.getLayerNum() != null).collect(Collectors.groupingBy(Task::getOrderId, Collectors.groupingBy(Task::getLayerNum)));
        orderIdToLayerNumberToTasks.forEach(
                (orderId, map) -> {
                    map.forEach((layerNumber, tasks) -> {
                        //看是否需要对tasks按照id进行排序
                        for (int i = 0; i < tasks.size(); i++) {
                            if (i != tasks.size() - 1) {
                                Task current = tasks.get(i);
                                Task next = tasks.get(i + 1);
                                current.setNextTask(next);
                                next.setPreTask(current);
                            }
                        }
                    });
                }
        );
        //对每个unit=1的套型任务的设置
        Map<String, List<Task>> orderIdToTasks =
                taskList.parallelStream().filter(task -> task.getLayerNum() == null).collect(Collectors.groupingBy(Task::getOrderId));
        orderIdToTasks.forEach((orderId, tasks) -> {
            for (int i = 0; i < tasks.size(); i++) {
                if (i != tasks.size() - 1) {
                    Task current = tasks.get(i);
                    Task next = tasks.get(i + 1);
                    current.setNextTask(next);
                    next.setPreTask(current);
                }
            }
        });
        //连接relatedLayer和套型的task
        for (Task task : taskList) {
            Integer unit = task.getUnit();
            if (unit == 1) {
                List<Integer> relatedLayer = task.getRelatedLayer();
                if (relatedLayer != null) {
                    String orderId = task.getOrderId();
                    Map<Integer, List<Task>> layerNumberToTasks = taskList.parallelStream().
                            filter(task1 -> task1.getLayerNum() != null && task1.getOrderId().equals(orderId) && relatedLayer.contains(task1.getLayerNum()))
                            .collect(Collectors.groupingBy(Task::getLayerNum));
                    List<List<Task>> taskGroups = layerNumberToTasks.values().stream().collect(Collectors.toList());
                    for (int i = 0; i < taskGroups.size(); i++) {
                        if (i != taskGroups.size() - 1) {
                            List<Task> preTasks = taskGroups.get(i);
                            Task preTask = preTasks.get(preTasks.size() - 1);
                            List<Task> nextTasks = taskGroups.get(i + 1);
                            Task nextTask = nextTasks.get(0);
                            preTask.setNextTask(nextTask);
                            nextTask.setPreTask(preTask);

                        } else {
                            List<Task> preTasks = taskGroups.get(i);
                            Task preTask = preTasks.get(preTasks.size() - 1);
                            preTask.setNextTask(task);
                            task.setPreTask(preTask);
                        }
                    }


                }

            }
        }

        return taskList;
    }

    public static List<Task> generateTaskListBak() {
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
