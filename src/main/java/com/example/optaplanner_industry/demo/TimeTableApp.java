package com.example.optaplanner_industry.demo;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.optaplanner_industry.demo.bootstrap.DataGenerator;
import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.solver.TimeTableConstraintProvider;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TimeTableApp {

    private static final LocalTime scheduleOne = LocalTime.of(0, 0, 0);
    private static final LocalTime scheduleTwo = LocalTime.of(8, 0, 0);
    private static final LocalTime scheduleThree = LocalTime.of(16, 0, 0);

    private static final int SCHEDULE_ONE = 0;
    private static final int SCHEDULE_TWO = 1;
    private static final int SCHEDULE_THREE = 2;


    private static int tempTotal = 0;
    private static OutputTask tempTask;

    private static int index;

    public static void main(String[] args) {
        SolverFactory<TimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Task.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        TimeTable problem = generateDemoData();

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);
        ScoreManager<TimeTable, HardSoftScore> scoreManager = ScoreManager.create(solverFactory);
        System.out.println(scoreManager.explainScore(solution));
        ScoreExplanation<TimeTable, HardSoftScore> scoreExplanation = scoreManager.explainScore(problem);
        HardSoftScore score = scoreExplanation.getScore();
        System.out.println(score);
        // Visualize the solution
        printTimetable(solution);
    }

    public static TimeTable generateDemoData() {
        List<ScheduleDate> scheduleDates = DataGenerator.generateScheduleDateList();
        List<ResourceItem> resourceItemList = DataGenerator.generateResources();
        List<Task> taskList = DataGenerator.generateTaskList();

        return new TimeTable(scheduleDates, resourceItemList, taskList);
    }

    private static void printTimetable(TimeTable timeTable) {
        List<Task> taskList = timeTable.getTaskList();

        Output out = new Output();
        out.setCode(200);
        out.setMessage("成功");
        out.setStatus(0);
        out.setRequestId(UUID.randomUUID().toString());

        List<OutputManufacturerOrder> manufacturerOrderList = new ArrayList<>();
        DataGenerator.getInput().getManufacturerOrderList().forEach(e -> {

            OutputManufacturerOrder outputManufacturerOrder = new OutputManufacturerOrder(e.getId(), e.getCode(),
                    e.getName(), e.getQuantity(), e.getPriority(), e.getType(), e.getRelatedManufactureOrderId(), e.getPeriod(), null);

            Product product = e.getProduct();
            OutputProduct oProduct = new OutputProduct(product.getId(), product.getCode(), product.getName(), new ArrayList<>());

            List<Step> steps = product.getStepList();
            List<OutputStep> oSteps = new ArrayList<>();
            for (Step step : steps) {
                OutputStep oStep = new OutputStep(step.getId(), step.getCode(), step.getName(), null, 0, step.getResourceRequirementList(), new ArrayList<>());
                oSteps.add(oStep);
            }

            oProduct.setStepList(oSteps);
            outputManufacturerOrder.setProduct(oProduct);
            manufacturerOrderList.add(outputManufacturerOrder);
        });

        out.setManufacturerOrderList(manufacturerOrderList);


        taskList.forEach(e -> {
//            System.out.println(e.getFullTaskName());
            LocalDateTime actualStartTime = e.getActualStartTime();
            LocalDateTime actualEndTime = e.getActualEndTime();

            Duration duration = Duration.between(actualStartTime, actualEndTime);
            long totalMinutes = duration.toMinutes();

            List<LocalDateTime[]> everyDay = getEveryDay(actualStartTime, actualEndTime);

            tempTotal = 0;
            index = 0;
            tempTask = null;
            everyDay.forEach(a -> {
                final LocalDateTime tempStartTime = a[0];
                final LocalDateTime tempEndTime = a[1];

                if (tempStartTime.toLocalTime().isBefore(scheduleTwo)) {
                    if (tempEndTime.toLocalTime().isAfter(scheduleThree)) {
                        setTask(out, e, tempStartTime, SCHEDULE_ONE, (int) (Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                scheduleTwo).toMinutes(), totalMinutes) + 1));
                        setTask(out, e, tempStartTime, SCHEDULE_TWO, (int) (Math.floorDiv(e.getQuantity() * Duration.between(scheduleTwo,
                                scheduleThree).toMinutes(), totalMinutes) + 1));
                        setTask(out, e, tempStartTime, SCHEDULE_THREE, (int) (Math.floorDiv(e.getQuantity() * Duration.between(scheduleThree,
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes) + 1));
                    } else if (tempEndTime.toLocalTime().isAfter(scheduleTwo)) {
                        setTask(out, e, tempStartTime, SCHEDULE_ONE, (int) (Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                scheduleTwo).toMinutes(), totalMinutes) + 1));
                        setTask(out, e, tempStartTime, SCHEDULE_TWO, (int) (Math.floorDiv(e.getQuantity() * Duration.between(scheduleTwo,
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes) + 1));
                    } else {
                        setTask(out, e, tempStartTime, SCHEDULE_ONE, (int) (Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes) + 1));
                    }

                } else if (tempStartTime.toLocalTime().isBefore(scheduleThree)) {
                    if (tempEndTime.toLocalTime().isAfter(scheduleThree)) {
                        setTask(out, e, tempStartTime, SCHEDULE_TWO, (int) (Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                scheduleThree).toMinutes(), totalMinutes) + 1));
                        setTask(out, e, tempStartTime, SCHEDULE_THREE, (int) (Math.floorDiv(e.getQuantity() * Duration.between(scheduleThree,
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes) + 1));
                    } else {
                        setTask(out, e, tempStartTime, SCHEDULE_TWO, (int) (Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes) + 1));
                    }

                } else {
                    setTask(out, e, tempStartTime, SCHEDULE_THREE, (int) (Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                            tempEndTime.toLocalTime()).toMinutes(), totalMinutes) + 1));
                }
            });

            if (tempTotal < e.getQuantity()) {
                tempTask.setAmount(tempTask.getAmount() + e.getQuantity() - tempTotal);
            } else if (tempTotal > e.getQuantity()) {
                tempTask.setAmount(e.getQuantity() - tempTotal + tempTask.getAmount());
            }

        });

        List<OutputTask> totalList = new ArrayList<>(1024);
        for (OutputManufacturerOrder mOrder : out.getManufacturerOrderList()) {
            OutputProduct product = mOrder.getProduct();
            product.getStepList().forEach(outputStep -> {
                List<OutputTask> collect = outputStep.getTaskList().stream().sorted((o1, o2) -> {
                            if (o1.getRunTime().isBefore(o2.getRunTime()))
                                return -1;
                            if (o1.getRunTime().isAfter(o2.getRunTime()))
                                return 1;
                            return 0;
                        }
                ).collect(Collectors.toList());
                outputStep.setStepStartTime(collect.get(0).getRunTime());
                outputStep.setExecutionDays(collect.get(collect.size() - 1).getRunTime().toEpochDay() -
                        collect.get(0).getRunTime().toEpochDay() + 1);

                totalList.addAll(collect);
            });

        }

        List<OutputTask> collect11 = totalList.stream().sorted((o1, o2) -> {
                    if (o1.getRunTime().isBefore(o2.getRunTime()))
                        return -1;
                    if (o1.getRunTime().isAfter(o2.getRunTime()))
                        return 1;
                    return 0;
                }
        ).collect(Collectors.toList());

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        collect11.forEach(e -> array.add(e.printInfo()));

        json.put("result", array);

        DataGenerator.writeResult(json);


        Map<String, List<OutputTask>> collect = collect11.stream().collect(Collectors.groupingBy(e -> "日期:" + e.getRunTime() + " 班次:" + e.getSchedule() + " 工序组:" + e.getStepId()));

        TreeMap<LocalDate, String> rrrr = new TreeMap<>((o1, o2) -> {
            if (o1.isBefore(o2))
                return -1;
            if (o1.isAfter(o2))
                return 1;
            return 0;
        });
        collect.forEach((k, v) -> {
//            System.out.println(k);
            int sum = v.stream().mapToInt(OutputTask::getAmount).sum();
//            System.out.println(k + " 生产总数:" + sum + " speed:" + v.get(0).getSpeed());
            String date = k.split(" ")[0].split(":")[1];
            String[] daaaa = date.split("-");
            rrrr.put(LocalDate.of(Integer.parseInt(daaaa[0]), Integer.parseInt(daaaa[1]), Integer.parseInt(daaaa[2])), k + " 生产总数:" + sum + " speed:" + v.get(0).getSpeed());
        });
        System.out.println("-----------------------------------------------");
//        rrrr.forEach((k, v) -> System.out.println(v));

        DataGenerator.writeObjectToFile(out);

//        taskList.sort(Comparator.comparingInt(Task::getStartTime));
//        taskList.stream().filter(e -> e.getManufactureOrderId().equals("manufactureOrder_0"))
//                .forEach(e -> System.out.println(e.getFullTaskName()));


//        Map<Integer, List<Task>> collect = taskList.stream().collect(Collectors.groupingBy(Task::getLayerNum));
//        collect.forEach((k, v) -> {
//            System.out.println("--------------" + k + "--------------");
//            v.sort(Comparator.comparingInt(Task::getStartTime));
//            v.forEach(e -> System.out.println(e.getFullTaskName()));
//        });

    }

    private static void setTask(Output out, Task task, LocalDateTime runTime, int schedule, Integer amount) {
        tempTotal += amount;
        OutputTask oTask = new OutputTask(task.getId(), index++, task.getCode(), task.getSpeed(), task.getUnit(),
                task.getLayerNum(), 0, task.getRelatedLayer(), amount, runTime.toLocalDate(), schedule, task.getStepId());
        tempTask = oTask;
        out.getManufacturerOrderList().get(Integer.parseInt(task.getManufactureOrderId().split("_")[1]))
                .getProduct().getStepList().get(Integer.parseInt(task.getStepId().split("_")[1]))
                .getTaskList().add(oTask);
    }

    private static List<LocalDateTime[]> getEveryDay(LocalDateTime actualStartTime, LocalDateTime actualEndTime) {
        List<LocalDateTime[]> result = new ArrayList<>();

        if (actualStartTime.toLocalDate().equals(actualEndTime.toLocalDate())) {
            result.add(new LocalDateTime[]{actualStartTime, actualEndTime});
        } else {
            for (LocalDate i = actualStartTime.toLocalDate(); i.isBefore(actualEndTime.toLocalDate().plusDays(1));
                 i = i.plusDays(1)) {
                if (i.equals(actualStartTime.toLocalDate())) {
                    result.add(new LocalDateTime[]{actualStartTime,
                            LocalDateTime.of(i.getYear(), i.getMonth(), i.getDayOfMonth(), 23, 59, 59)});
                } else if (i.equals(actualEndTime.toLocalDate())) {
                    result.add(new LocalDateTime[]{LocalDateTime.of(i.getYear(), i.getMonth(), i.getDayOfMonth(),
                            0, 0, 0), actualEndTime});
                } else {
                    result.add(new LocalDateTime[]{LocalDateTime.of(i.getYear(), i.getMonth(), i.getDayOfMonth(), 0, 0, 0),
                            LocalDateTime.of(i.getYear(), i.getMonth(), i.getDayOfMonth(), 23, 59, 59)});
                }
            }
        }

        return result;
    }


}
