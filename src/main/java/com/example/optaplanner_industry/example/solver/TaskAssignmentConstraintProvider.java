package com.example.optaplanner_industry.example.solver;

import com.example.optaplanner_industry.example.domain.Task;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.Objects;

public class TaskAssignmentConstraintProvider  implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                typeConflict(constraintFactory),
                workgroupConflict(constraintFactory)
//                workerConflict(constraintFactory),
//                taskOrderConflict(constraintFactory),
//                workerMatchMachine(constraintFactory)
                // Soft constraints
//                teacherRoomStability(constraintFactory),
//                teacherTimeEfficiency(constraintFactory),
//                studentGroupSubjectVariety(constraintFactory)
        };
    }

    Constraint typeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal((task) -> Objects.equals(task.getRequiredYarnType(), task.getWorkGroup().getYarnType())))
//                .filter((task1, task2) -> Objects.equals(task1.getSubject(), task2.getMachine().getName()))
                .penalize("Teacher room stability", HardSoftScore.ofHard(5));
    }
    Constraint workgroupConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getWorkGroup))
//                .filter((task1, task2) -> Objects.equals(task1.getSubject(), task2.getMachine().getName()))
                .penalize("Teacher room stability", HardSoftScore.ofHard(2));
    }

    Constraint capacityConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Task.class,
                        Joiners.equal(Task::getWorkGroup))
//                .filter((task1, task2) -> Objects.equals(task1.getSubject(), task2.getMachine().getName()))
                .penalize("Teacher room stability", HardSoftScore.ofHard(2));
    }
}
