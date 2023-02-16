package com.example.optaplanner_industry.example;

import com.example.optaplanner_industry.example.domain.Task;
import com.example.optaplanner_industry.example.domain.TaskAssignment;
import com.example.optaplanner_industry.example.domain.WorkGroup;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
        startPlan();
    }

    private static void startPlan() {
        List<WorkGroup> WorkGroups = getWorkGroups();
        List<Task> tasks = getTasks();

//        String ins = App.class.getResource("/1.xml");
        String ins = "1.xml";
        SolverFactory<TaskAssignment> solverFactory = SolverFactory.createFromXmlResource(ins);
        Solver<TaskAssignment> solver = solverFactory.buildSolver();
        TaskAssignment unassignment = new TaskAssignment(WorkGroups, tasks);

        TaskAssignment assigned = solver.solve(unassignment);//启动引擎

        List<WorkGroup> WorkGroupsAssigned = assigned.getTaskList().stream().map(Task::getWorkGroup).distinct().collect(Collectors.toList());
        for (WorkGroup WorkGroup : WorkGroupsAssigned) {
            System.out.print("\n" + WorkGroup + ":");
            List<Task> tasksInWorkGroup = assigned.getTaskList().stream().filter(x -> x.getWorkGroup().equals(WorkGroup)).collect(Collectors.toList());
            for (Task task : tasksInWorkGroup) {
                System.out.print("->" + task);
            }
        }
    }


    private static List<WorkGroup> getWorkGroups() {
        // 六个机台
        List<WorkGroup> WorkGroups = new ArrayList<>();
        WorkGroups.add(new WorkGroup(1, "Type_A", 300, 100));
        WorkGroups.add(new WorkGroup(2, "Type_A", 1000, 100));
        WorkGroups.add(new WorkGroup(3, "TYPE_B", 1000, 300));
        WorkGroups.add(new WorkGroup(4, "TYPE_B", 1000, 100));
        WorkGroups.add(new WorkGroup(5, "Type_C", 1100, 100));
        WorkGroups.add(new WorkGroup(6, "Type_D", 900, 100));

        return WorkGroups;
    }

    private static List<Task> getTasks() {
        // 10个任务
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "Type_A", 100));
        tasks.add(new Task(2, "Type_A", 100));
        tasks.add(new Task(3, "Type_A", 100));
        tasks.add(new Task(4, "Type_A", 100));
        tasks.add(new Task(5, "TYPE_B", 800));
        tasks.add(new Task(6, "TYPE_B", 500));
        tasks.add(new Task(7, "Type_C", 800));
        tasks.add(new Task(8, "Type_C", 300));
        tasks.add(new Task(9, "Type_D", 400));
        tasks.add(new Task(10, "Type_D", 500));

        return tasks;
    }
}