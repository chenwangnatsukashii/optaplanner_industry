package com.example.optaplanner_industry.industry.solver;

import com.example.optaplanner_industry.industry.domain.Task;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class IndustryTimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                workerConflict(constraintFactory),
                machineConflict(constraintFactory),
                // Soft constraints
        };
    }


    Constraint workerConflict(ConstraintFactory constraintFactory) {
        // A worker can operate at most one task at the same time.
        return constraintFactory
                // Select each pair of 2 different lessons ...
                .forEachUniquePair(Task.class,
                        // ... in the same timeslot ...
                        Joiners.equal(Task::getProcessTimeslot),
                        // ... in the same room ...
                        Joiners.equal(Task::getWorker))
                // ... and penalize each pair with a hard weight.
                .penalize("Worker conflict", HardSoftScore.ONE_HARD);
    }

    Constraint machineConflict(ConstraintFactory constraintFactory) {
        // A machine can work at most one task at the same time.
        return constraintFactory
                // Select each pair of 2 different lessons ...
                .forEachUniquePair(Task.class,
                        // ... in the same timeslot ...
                        Joiners.equal(Task::getProcessTimeslot),
                        // ... in the same room ...
                        Joiners.equal(Task::getMachine))
                // ... and penalize each pair with a hard weight.
                .penalize("WorkGroup conflict", HardSoftScore.ONE_HARD);
    }

//    Constraint taskOrderConflict(ConstraintFactory constraintFactory) {
//        // The taskOrder is fixed
//        return constraintFactory
//                // Select each pair of 2 different lessons ...
//                .forEachUniquePair(Task.class,
//                        // ... in the same timeslot ...
//                        Joiners.equal(Task::getProcessTimeslot),
//                        // ... in the same room ...
//                        Joiners.equal(Task::getWorker))
//                // ... and penalize each pair with a hard weight.
//                .penalize("Worker conflict", HardSoftScore.ONE_HARD);
//    }
}
