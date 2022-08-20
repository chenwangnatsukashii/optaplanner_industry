package com.example.optaplanner_industry.demo;


import com.example.optaplanner_industry.demo.domain.Task;
import com.example.optaplanner_industry.demo.domain.Machine;
import com.example.optaplanner_industry.demo.domain.TimeTable;
import com.example.optaplanner_industry.demo.domain.Timeslot;
import com.example.optaplanner_industry.demo.solver.TimeTableConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        List<Machine> machineList = new ArrayList<>(4);
        machineList.add(new Machine("机器01"));
        machineList.add(new Machine("机器02"));
        machineList.add(new Machine("机器03"));
        machineList.add(new Machine("机器04"));

        List<Task> taskList = new ArrayList<>();
        long id = 0;
        taskList.add(new Task(id++, "机器01", "工人01", "工序01"));
        taskList.add(new Task(id++, "机器02", "工人02", "工序02"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器04", "工人04", "工序04"));
        taskList.add(new Task(id++, "机器02", "工人02", "工序02"));
        taskList.add(new Task(id++, "机器01", "工人01", "工序01"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器02", "工人02", "工序02"));
        taskList.add(new Task(id++, "机器01", "工人01", "工序01"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器02", "工人02", "工序02"));
        taskList.add(new Task(id++, "机器01", "工人01", "工序01"));
        taskList.add(new Task(id++, "机器02", "工人02", "工序02"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器01", "工人01", "工序01"));
        taskList.add(new Task(id++, "机器03", "工人03", "工序03"));
        taskList.add(new Task(id++, "机器02", "工人02", "工序02"));
        taskList.add(new Task(id, "机器02", "工人02", "工序02"));

        return new TimeTable(timeslotList, machineList, taskList);
    }

    private static void printTimetable(TimeTable timeTable) {
        LOGGER.info("");
        List<Machine> machineList = timeTable.getMachineList();
        List<Task> taskList = timeTable.getTaskList();
        Map<Timeslot, Map<Machine, List<Task>>> lessonMap = taskList.stream()
                .filter(task -> task.getTimeslot() != null && task.getMachine() != null)
                .collect(Collectors.groupingBy(Task::getTimeslot, Collectors.groupingBy(Task::getMachine)));
        LOGGER.info("|            | " + machineList.stream()
                .map(machine -> String.format("%-10s", machine.getName())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "------------|".repeat(machineList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            List<List<Task>> cellList = machineList.stream()
                    .map(machine -> {
                        Map<Machine, List<Task>> byRoomMap = lessonMap.get(timeslot);
                        if (byRoomMap == null) {
                            return Collections.<Task>emptyList();
                        }
                        List<Task> cellTaskList = byRoomMap.get(machine);
                        if (cellTaskList == null) {
                            return Collections.<Task>emptyList();
                        }
                        return cellTaskList;
                    })
                    .collect(Collectors.toList());

            LOGGER.info("| " + String.format("%-10s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Task::getSubject).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Task::getWorker).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Task::getTaskOrder).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|" + "------------|".repeat(machineList.size() + 1));
        }
        List<Task> unassignedTasks = taskList.stream()
                .filter(task -> task.getTimeslot() == null || task.getMachine() == null)
                .collect(Collectors.toList());
        if (!unassignedTasks.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned lessons");
            for (Task task : unassignedTasks) {
                LOGGER.info("  " + task.getSubject() + " - " + task.getWorker() + " - " + task.getTaskOrder());
            }
        }
    }

}
