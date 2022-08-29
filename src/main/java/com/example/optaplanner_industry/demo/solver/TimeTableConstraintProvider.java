package com.example.optaplanner_industry.demo.solver;

import com.example.optaplanner_industry.demo.domain.Task;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.time.Duration;
import java.util.Objects;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                workGroupConflict(constraintFactory),
//                workConflict(constraintFactory),
//                studentGroupConflict(constraintFactory),
                workerGroupMatch(constraintFactory)
                // Soft constraints
//                teacherRoomStability(constraintFactory),
//                teacherTimeEfficiency(constraintFactory),
//                studentGroupSubjectVariety(constraintFactory)
        };
    }

    // 同一时刻，同一工序只能在对应工作组生产
    Constraint workGroupConflict(ConstraintFactory constraintFactory) {
        // A machine can work at most one task at the same time.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getTimeslot),
                        Joiners.equal(Task::getWorkGroup))
                .penalize("WorkGroup conflict", HardSoftScore.ONE_HARD);
    }

//    Constraint workConflict(ConstraintFactory constraintFactory) {
//        // A worker can operate at most one machine at the same time.
//        return constraintFactory
//                .forEachUniquePair(Task.class,
//                        Joiners.equal(Task::getTimeslot),
//                        Joiners.equal(Task::getWorker))
//                .penalize("Worker conflict", HardSoftScore.ONE_HARD);
//    }

    Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getTimeslot),
                        Joiners.equal(Task::getTaskName))
                .penalize("Student group conflict", HardSoftScore.ONE_HARD);
    }

    // 某一工序只能在对应工作组生产
    Constraint workerGroupMatch(ConstraintFactory constraintFactory) {
        // A worker must match to a specific machine.
        return constraintFactory
                .forEachUniquePair(Task.class,
//                        Joiners.equal(Task::getTimeslot),
                        Joiners.equal((task) -> !Objects.equals(task.getTaskName(), task.getWorkGroup().getName())))
//                .filter((task1, task2) -> Objects.equals(task1.getSubject(), task2.getWorkGroup().getName()))
                .reward("Teacher room stability", HardSoftScore.ofHard(5));
    }

//    Constraint teacherRoomStability(ConstraintFactory constraintFactory) {
//        // A teacher prefers to teach in a single room.
//        return constraintFactory
//                .forEachUniquePair(Task.class,
//                        Joiners.equal(Task::getWorker))
//                .filter((task1, task2) -> task1.getWorkGroup() != task2.getWorkGroup())
//                .penalize("Teacher room stability", HardSoftScore.ONE_SOFT);
//    }
//
//    Constraint teacherTimeEfficiency(ConstraintFactory constraintFactory) {
//        // A teacher prefers to teach sequential lessons and dislikes gaps between lessons.
//        return constraintFactory
//                .forEach(Task.class)
//                .join(Task.class, Joiners.equal(Task::getWorker),
//                        Joiners.equal((task) -> task.getTimeslot().getDayOfWeek()))
//                .filter((task1, task2) -> {
//                    Duration between = Duration.between(task1.getTimeslot().getEndTime(),
//                            task2.getTimeslot().getStartTime());
//                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
//                })
//                .reward("Teacher time efficiency", HardSoftScore.ONE_SOFT);
//    }

    Constraint studentGroupSubjectVariety(ConstraintFactory constraintFactory) {
        // A student group dislikes sequential lessons on the same subject.
        return constraintFactory
                .forEach(Task.class)
                .join(Task.class,
                        Joiners.equal(Task::getTaskName),
                        Joiners.equal(Task::getTaskOrder),
                        Joiners.equal((task) -> task.getTimeslot().getDayOfWeek()))
                .filter((task1, task2) -> {
                    Duration between = Duration.between(task1.getTimeslot().getEndTime(),
                            task2.getTimeslot().getStartTime());
                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
                })
                .penalize("Student group subject variety", HardSoftScore.ONE_SOFT);
    }

}
