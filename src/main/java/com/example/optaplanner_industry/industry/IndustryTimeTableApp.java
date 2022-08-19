package com.example.optaplanner_industry.industry;


import com.example.optaplanner_industry.demo.domain.Lesson;
import com.example.optaplanner_industry.demo.domain.Room;
import com.example.optaplanner_industry.demo.domain.TimeTable;
import com.example.optaplanner_industry.industry.builder.IndustryResourceBuilder;
import com.example.optaplanner_industry.industry.builder.IndustryTaskBuilder;
import com.example.optaplanner_industry.industry.builder.ResourceBuilder;
import com.example.optaplanner_industry.industry.builder.TaskBuilder;
import com.example.optaplanner_industry.industry.domain.*;
import com.example.optaplanner_industry.industry.solver.IndustryTimeTableConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndustryTimeTableApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndustryTimeTableApp.class);

    public static void main(String[] args) {
        SolverFactory<IndustryTimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(IndustryTimeTable.class)
                .withEntityClasses(Task.class)
                .withConstraintProviderClass(IndustryTimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        IndustryTimeTable problem = generateDemoData();

        // Solve the problem
        Solver<IndustryTimeTable> solver = solverFactory.buildSolver();
        IndustryTimeTable solution = solver.solve(problem);

        // Visualize the solution
        printTimetable(solution);
    }

    public static IndustryTimeTable generateDemoData() {
        List<ProcessTimeslot> ProcessTimeslotList = new ArrayList<>(10);
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        ProcessTimeslotList.add(new ProcessTimeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));


        ResourceBuilder machineBuilder = new IndustryResourceBuilder();
        List<Resource> machineList = machineBuilder
                .addMachine(new String[]{"Resource01-1", "Resource01-2"}, 2, new Integer[]{1}, 400)
                .addMachine(new String[]{"Resource02-1"}, 1, new Integer[]{2}, 200)
                .addMachine(new String[]{"Resource03-1", "Resource03-2", "Resource03-3"}, 3, new Integer[]{3}, 100)
                .addMachine(new String[]{"Resource04-1", "Resource04-2", "Resource04-3", "Resource04-4"}, 4, new Integer[]{4}, 300)
                .builderResource();

        ResourceBuilder workerBuilder = new IndustryResourceBuilder();
        List<Resource> workerList = workerBuilder
                .addWorker("worker01", new Integer[]{1, 2}, 1)
                .addWorker("worker02", new Integer[]{2}, 1)
                .addWorker("worker03", new Integer[]{1, 3}, 1)
                .addWorker("worker04", new Integer[]{3, 4}, 1)
                .builderResource();

        TaskBuilder taskBuilder = new IndustryTaskBuilder();
        List<Task> taskList = taskBuilder
                .addTask("工单01(20220819-01)", 300, 10, LocalDate.of(2022, 8, 20), LocalDate.of(2022, 8, 30), 10)
                .addTask("工单02(20220819-02)", 500, 6, LocalDate.of(2022, 8, 24), LocalDate.of(2022, 8, 28), 4)
                .builderTask();


        return new IndustryTimeTable(ProcessTimeslotList, workerList, machineList, taskList);
    }

    private static void printTimetable(IndustryTimeTable timeTable) {
        LOGGER.info("");
        List<Resource> machineList = timeTable.getMachineList();
        List<Task> taskList = timeTable.getTaskList();
        Map<ProcessTimeslot, Map<Resource, List<Task>>> taskMap = taskList.stream()
                .filter(task -> task.getProcessTimeslot() != null && task.getMachine() != null)
                .collect(Collectors.groupingBy(Task::getProcessTimeslot, Collectors.groupingBy(Task::getMachine)));
        LOGGER.info("|            | " + machineList.stream()
                .map(machine -> String.format("%-10s", machine.getResourceCode())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "------------|".repeat(machineList.size() + 1));
        for (ProcessTimeslot ProcessTimeslot : timeTable.getProcessTimeslotList()) {
            List<List<Task>> cellList = machineList.stream()
                    .map(room -> {
                        Map<Resource, List<Task>> byRoomMap = taskMap.get(ProcessTimeslot);
                        if (byRoomMap == null) {
                            return Collections.<Task>emptyList();
                        }
                        List<Task> cellLessonList = byRoomMap.get(room);
                        if (cellLessonList == null) {
                            return Collections.<Task>emptyList();
                        }
                        return cellLessonList;
                    })
                    .collect(Collectors.toList());

            LOGGER.info("| " + String.format("%-10s",
                    ProcessTimeslot.getDayOfWeek().toString().substring(0, 3) + " " + ProcessTimeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Task::getTaskName).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
//            LOGGER.info("|            | "
//                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
//                            cellLessonList.stream().map(Task::getQuantity).collect(Collectors.joining(", "))))
//                    .collect(Collectors.joining(" | "))
//                    + " |");
//            LOGGER.info("|            | "
//                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
//                            cellLessonList.stream().map(Task::getLayerNumber).collect(Collectors.joining(", "))))
//                    .collect(Collectors.joining(" | "))
//                    + " |");
            LOGGER.info("|" + "------------|".repeat(machineList.size() + 1));
        }
        List<Task> unassignedLessons = taskList.stream()
                .filter(task -> task.getProcessTimeslot() == null || task.getMachine() == null)
                .collect(Collectors.toList());
        if (!unassignedLessons.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned lessons");
            for (Task task : unassignedLessons) {
                LOGGER.info("  " + task.getTaskName() + " - " + task.getQuantity() + " - " + task.getLayerNumber());
            }
        }
    }

}
