package com.example.optaplanner_industry.industry.solver;

import com.example.optaplanner_industry.domain.Lesson;
import com.example.optaplanner_industry.industry.entity.Task;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class IndustryTimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[0];
    }

    // 一个工人在同一个时刻只能操作一台设备
    Constraint workerConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one lesson at the same time.
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
}
