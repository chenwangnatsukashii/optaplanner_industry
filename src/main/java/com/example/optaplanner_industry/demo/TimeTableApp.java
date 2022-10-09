package com.example.optaplanner_industry.demo;


import com.example.optaplanner_industry.demo.bootstrap.DataGenerator;
import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.CloneUtils;
import com.example.optaplanner_industry.demo.solver.TimeTableConstraintProvider;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
//        taskList.sort(Comparator.comparingInt(Task::getStartTime));

        Output out = new Output();
        out.setCode(200);
        out.setMessage("成功");
        out.setStatus(0);

        List<OutputManufacturerOrder> manufacturerOrderList = new ArrayList<>();
        DataGenerator.getInput().getManufacturerOrderList().forEach(e -> {

            OutputManufacturerOrder outputManufacturerOrder = new OutputManufacturerOrder(e.getId(), e.getCode(),
                    e.getName(), e.getQuantity(), e.getPriority(), e.getType(), e.getRelatedManufactureOrderId(), e.getPeriod(), null);

            Product product = e.getProduct();
            OutputProduct oProduct = new OutputProduct(product.getId(), product.getCode(), product.getName(), new ArrayList<>());

            List<Step> steps = product.getStepList();
            List<OutputStep> oSteps = new ArrayList<>();
            for (Step step : steps) {
                OutputStep oStep = new OutputStep(step.getId(), step.getCode(), step.getName(), step.getResourceRequirementList(), new ArrayList<>());
                oSteps.add(oStep);
            }

            oProduct.setStepList(oSteps);
            outputManufacturerOrder.setProduct(oProduct);
            manufacturerOrderList.add(outputManufacturerOrder);
        });

        out.setManufacturerOrderList(manufacturerOrderList);


        taskList.forEach(e -> {
            LocalDateTime actualStartTime = e.getActualStartTime();
            LocalDateTime actualEndTime = e.getActualEndTime();

            Duration duration = Duration.between(actualStartTime, actualEndTime);

            long totalMinutes = duration.toMinutes();

            List<LocalDateTime[]> everyDay = getEveryDay(actualStartTime, actualEndTime);

            if (tempTotal != 0 && tempTotal < e.getQuantity()) {
                tempTask.setAmount(e.getQuantity() - tempTotal);
            }
            tempTotal = 0;
            index = 0;
            everyDay.forEach(a -> {
                final LocalDateTime tempStartTime = a[0];
                final LocalDateTime tempEndTime = a[1];

                if (tempStartTime.toLocalTime().isBefore(scheduleTwo)) {
                    if (tempEndTime.toLocalTime().isAfter(scheduleThree)) {
                        setTask(out, e, tempStartTime, 0, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                scheduleTwo).toMinutes(), totalMinutes)));
                        setTask(out, e, tempStartTime, 1, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(scheduleTwo,
                                scheduleThree).toMinutes(), totalMinutes)));
                        setTask(out, e, tempStartTime, 2, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(scheduleThree,
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes)));
                    } else if (tempEndTime.toLocalTime().isAfter(scheduleTwo)) {
                        setTask(out, e, tempStartTime, 0, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                scheduleTwo).toMinutes(), totalMinutes)));
                        setTask(out, e, tempStartTime, 1, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(scheduleTwo,
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes)));
                    } else {
                        setTask(out, e, tempStartTime, 0, e.getQuantity());
                    }

                } else if (tempStartTime.toLocalTime().isBefore(scheduleThree)) {
                    if (tempEndTime.toLocalTime().isAfter(scheduleThree)) {
                        setTask(out, e, tempStartTime, 1, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(tempStartTime.toLocalTime(),
                                scheduleThree).toMinutes(), totalMinutes)));
                        setTask(out, e, tempStartTime, 2, (int) Math.ceil(Math.floorDiv(e.getQuantity() * Duration.between(scheduleThree,
                                tempEndTime.toLocalTime()).toMinutes(), totalMinutes)));
                    } else {
                        setTask(out, e, tempStartTime, 1, e.getQuantity());
                    }

                } else {
                    setTask(out, e, tempStartTime, 2, e.getQuantity());
                }
            });

        });


        DataGenerator.writeObjectToFile(out);

//        Map<Integer, List<Task>> collect = taskList.stream().collect(Collectors.groupingBy(Task::getLayerNum));
//
//        collect.forEach((k, v) -> {
//            System.out.println("--------------" + k + "--------------");
//            v.sort(Comparator.comparingInt(Task::getStartTime));
//            v.forEach(e -> System.out.println(e.getFullTaskName()));
//        });

//        taskList.forEach(e -> System.out.println(e.getFullTaskName()));

    }

    private static void setTask(Output out, Task task, LocalDateTime runTime, int schedule, Integer amount) {
        tempTotal += amount;
        OutputTask oTask = new OutputTask(task.getId(), index++, task.getCode(), task.getSpeed(), task.getUnit(), task.getLayerNum(), 0, task.getRelatedLayer(), amount, runTime.toLocalDate(), schedule);
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
            for (LocalDate i = actualStartTime.toLocalDate(); i.isBefore(actualEndTime.toLocalDate());
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
