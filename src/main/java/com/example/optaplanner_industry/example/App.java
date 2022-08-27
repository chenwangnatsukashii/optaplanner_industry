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

    private static void startPlan(){
        List<WorkGroup> WorkGroups = getWorkGroups();
        List<Task> tasks = getTasks();

//        String ins = App.class.getResource("/1.xml");
        String ins = "1.xml";
        SolverFactory<TaskAssignment> solverFactory = SolverFactory.createFromXmlResource(ins);
        Solver<TaskAssignment> solver = solverFactory.buildSolver();
        TaskAssignment unassignment = new TaskAssignment(WorkGroups, tasks);

        TaskAssignment assigned = solver.solve(unassignment);//启动引擎

        List<WorkGroup> WorkGroupsAssigned = assigned.getTaskList().stream().map(Task::getWorkGroup).distinct().collect(Collectors.toList());
        for(WorkGroup WorkGroup : WorkGroupsAssigned) {
            System.out.print("\n" + WorkGroup + ":");
            List<Task> tasksInWorkGroup = assigned.getTaskList().stream().filter(x -> x.getWorkGroup().equals(WorkGroup)).collect(Collectors.toList());
            for(Task task : tasksInWorkGroup) {
                System.out.print("->" + task);
            }
        }
    }


    private static List<WorkGroup> getWorkGroups() {
        // 六个机台
        WorkGroup m1 = new WorkGroup(1, "Type_A", 300, 100);
        WorkGroup m2 = new WorkGroup(2, "Type_A", 1000, 100);
        WorkGroup m3 = new WorkGroup(3, "TYPE_B", 1000, 300);
        WorkGroup m4 = new WorkGroup(4, "TYPE_B", 1000, 100);
        WorkGroup m5 = new WorkGroup(5, "Type_C", 1100, 100);
        WorkGroup m6 = new WorkGroup(6, "Type_D", 900, 100);

        List<WorkGroup> WorkGroups = new ArrayList<WorkGroup>();
        WorkGroups.add(m1);
        WorkGroups.add(m2);
        WorkGroups.add(m3);
        WorkGroups.add(m4);
        WorkGroups.add(m5);
        WorkGroups.add(m6);

        return WorkGroups;
    }

    private static List<Task> getTasks(){
        // 10个任务
        Task t1 = new Task(1, "Type_A", 100);
        Task t2 = new Task(2, "Type_A", 100);
        Task t3 = new Task(3, "Type_A", 100);
        Task t4 = new Task(4, "Type_A", 100);
        Task t5 = new Task(5, "TYPE_B", 800);
        Task t6 = new Task(6, "TYPE_B", 500);
        Task t7 = new Task(7, "Type_C", 800);
        Task t8 = new Task(8, "Type_C", 300);
        Task t9 = new Task(9, "Type_D", 400);
        Task t10 = new Task(10, "Type_D", 500);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        tasks.add(t4);
        tasks.add(t5);
        tasks.add(t6);
        tasks.add(t7);
        tasks.add(t8);
        tasks.add(t9);
        tasks.add(t10);

        return tasks;
    }
}