package com.example.optaplanner_industry.demo;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.optaplanner_industry.demo.domain.*;
import com.example.optaplanner_industry.demo.jsonUtils.LoadFile;
import com.example.optaplanner_industry.demo.solver.TimeTableConstraintProvider;
import com.example.optaplanner_industry.industry.builder.IndustryTaskBuilder;
import com.example.optaplanner_industry.industry.builder.TaskBuilder;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TimeTableApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableApp.class);

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

        // Visualize the solution
        printTimetable(solution);
    }

    public static TimeTable generateDemoData() {
        Input inputJson = LoadFile.readJsonFile("json/input_1.json");


        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        List<WorkGroup> workGroupList = new ArrayList<>(8);
        workGroupList.add(new WorkGroup("工作组01"));
        workGroupList.add(new WorkGroup("工作组02"));
        workGroupList.add(new WorkGroup("工作组03"));
        workGroupList.add(new WorkGroup("工作组04"));


        TaskBuilder taskBuilder = new IndustryTaskBuilder();
        List<Task> taskList = taskBuilder
                .addTask("产品01", 300, 2, LocalDate.of(2022, 8, 20), LocalDate.of(2022, 8, 30), 10)
                .addTask("产品02", 500, 3, LocalDate.of(2022, 8, 24), LocalDate.of(2022, 8, 28), 4)
                .builderTask();


        return new TimeTable(timeslotList, workGroupList, taskList);
    }

    private static void printTimetable(TimeTable timeTable) {
        LOGGER.info("");
        List<WorkGroup> workGroupList = timeTable.getWorkGroupList();
        List<Task> taskList = timeTable.getTaskList();
        Map<Timeslot, Map<WorkGroup, List<Task>>> lessonMap = taskList.stream()
                .filter(task -> task.getTimeslot() != null && task.getWorkGroup() != null)
                .collect(Collectors.groupingBy(Task::getTimeslot, Collectors.groupingBy(Task::getWorkGroup)));
        LOGGER.info("|            | " + workGroupList.stream()
                .map(workGroup -> String.format("%-10s", workGroup.getName())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "------------|".repeat(workGroupList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            List<List<Task>> cellList = workGroupList.stream()
                    .map(workGroup -> {
                        Map<WorkGroup, List<Task>> byRoomMap = lessonMap.get(timeslot);
                        if (byRoomMap == null) {
                            return Collections.<Task>emptyList();
                        }
                        List<Task> cellTaskList = byRoomMap.get(workGroup);
                        return Objects.requireNonNullElse(cellTaskList, Collections.<Task>emptyList());
                    })
                    .collect(Collectors.toList());

            LOGGER.info("| " + String.format("%-10s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Task::getCode).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(e -> "第0" + e.getLayerNum() + "层").collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
//            LOGGER.info("|            | "
//                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
//                            cellLessonList.stream().map(Task::getTaskOrder).collect(Collectors.joining(", "))))
//                    .collect(Collectors.joining(" | "))
//                    + " |");
            LOGGER.info("| " + String.format("%-10s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getEndTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Task::getTaskOrder).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|" + "------------|".repeat(workGroupList.size() + 1));
        }
        List<Task> unassignedTasks = taskList.stream()
                .filter(task -> task.getTimeslot() == null || task.getWorkGroup() == null)
                .collect(Collectors.toList());
        if (!unassignedTasks.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned lessons");
            for (Task task : unassignedTasks) {
                LOGGER.info("  " + task.getCode() + " - " + task.getTaskOrder());
            }
        }
    }

}
