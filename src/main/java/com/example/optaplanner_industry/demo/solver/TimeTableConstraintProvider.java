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
                machineConflict(constraintFactory),
                workerConflict(constraintFactory),
                taskOrderConflict(constraintFactory),
                workerMatchMachine(constraintFactory)
                // Soft constraints
//                teacherRoomStability(constraintFactory),
//                teacherTimeEfficiency(constraintFactory),
//                studentGroupSubjectVariety(constraintFactory)
        };
    }

    Constraint machineConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getTimeslot),
                        Joiners.equal(Task::getMachine))
                .penalize("Machine conflict", HardSoftScore.ONE_HARD);
    }

    Constraint workerConflict(ConstraintFactory constraintFactory) {
        // A worker can operate at most one machine at the same time.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getTimeslot),
                        Joiners.equal(Task::getWorker))
                .penalize("Worker conflict", HardSoftScore.ONE_HARD);
    }

    Constraint taskOrderConflict(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getTimeslot),
                        Joiners.equal(Task::getTaskOrder))
                .penalize("Task order conflict", HardSoftScore.ONE_HARD);
    }

    Constraint workerMatchMachine(ConstraintFactory constraintFactory) {
        // A worker must match to a specific machine.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getSubject),
                        Joiners.equal((task) ->Objects.equals(task.getSubject(), task.getMachine().getName())))
//                .filter((task1, task2) -> Objects.equals(task1.getSubject(), task2.getMachine().getName()))
                .penalize("Teacher room stability", HardSoftScore.ofHard(5));
    }

    Constraint teacherRoomStability(ConstraintFactory constraintFactory) {
        // A teacher prefers to teach in a single room.
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getWorker))
                .filter((task1, task2) -> task1.getMachine() != task2.getMachine())
                .penalize("Teacher room stability", HardSoftScore.ONE_SOFT);
    }

    Constraint teacherTimeEfficiency(ConstraintFactory constraintFactory) {
        // A teacher prefers to teach sequential lessons and dislikes gaps between lessons.
        return constraintFactory
                .forEach(Task.class)
                .join(Task.class, Joiners.equal(Task::getWorker),
                        Joiners.equal((task) -> task.getTimeslot().getDayOfWeek()))
                .filter((task1, task2) -> {
                    Duration between = Duration.between(task1.getTimeslot().getEndTime(),
                            task2.getTimeslot().getStartTime());
                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
                })
                .reward("Teacher time efficiency", HardSoftScore.ONE_SOFT);
    }

    Constraint studentGroupSubjectVariety(ConstraintFactory constraintFactory) {
        // A student group dislikes sequential lessons on the same subject.
        return constraintFactory
                .forEach(Task.class)
                .join(Task.class,
                        Joiners.equal(Task::getSubject),
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
