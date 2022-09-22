package com.example.optaplanner_industry.demo.solver;

import com.example.optaplanner_industry.demo.domain.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;

import java.time.Duration;
import java.util.Objects;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
//                workGroupConflict(constraintFactory),
                sameLayerTaskOrderConflict(constraintFactory),
//                sameStepResourceConflict(constraintFactory)
//                workConflict(constraintFactory),
//                studentGroupConflict(constraintFactory),
//                workerGroupMatch(constraintFactory)
//                timeConflict(constraintFactory),
//                delayDaysConflict(constraintFactory),
//                differentLayerConflict(constraintFactory),
//                sameLayerConflict(constraintFactory),
//                // Soft constraints
//                exchangeTimeConflict(constraintFactory),
//                priorityConflict(constraintFactory)
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
                        Joiners.equal(Task::getScheduleDate),
                        Joiners.equal(Task::getResourceItem))
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
                        Joiners.equal(Task::getScheduleDate),
                        Joiners.equal(Task::getCode))
                .penalize("Student group conflict", HardSoftScore.ONE_HARD);
    }

    // 某一工序只能在对应工作组生产
//    Constraint workerGroupMatch(ConstraintFactory constraintFactory) {
//        // A worker must match to a specific machine.
//        return constraintFactory
//                .forEachUniquePair(Task.class,
////                        Joiners.equal(Task::getTimeslot),
//                        Joiners.equal((task) -> !Objects.equals(task.getCode(), task.get().getName())))
////                .filter((task1, task2) -> Objects.equals(task1.getSubject(), task2.getWorkGroup().getName()))
//                .reward("Teacher room stability", HardSoftScore.ofHard(5));
//    }

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

//    Constraint studentGroupSubjectVariety(ConstraintFactory constraintFactory) {
//        // A student group dislikes sequential lessons on the same subject.
//        return constraintFactory
//                .forEach(Task.class)
//                .join(Task.class,
//                        Joiners.equal(Task::getCode),
//                        Joiners.equal(Task::getTaskOrder),
//                        Joiners.equal((task) -> task.getTimeslot().getDayOfWeek()))
//                .filter((task1, task2) -> {
//                    Duration between = Duration.between(task1.getTimeslot().getEndTime(),
//                            task2.getTimeslot().getStartTime());
//                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
//                })
//                .penalize("Student group subject variety", HardSoftScore.ONE_SOFT);
//    }

    //Hard constraint
    Constraint sameLayerTaskOrderConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Task.class,Joiners.equal(Task::getLayerNum),Joiners.filtering((task1,task2)->
                        task2.getStepIndex()-task1.getStepIndex()==1))

                .filter((task1,task2)->{
                    System.out.println("task1："+task1.getScheduleDate().getLocalDateTime()+"task2:"+task2.getScheduleDate().getLocalDateTime());
                           return task1.getScheduleDate().getLocalDateTime().isAfter(task2.getScheduleDate().getLocalDateTime());

                        }
                        )
                .penalize("1",HardSoftScore.ofHard(200));

    }

    Constraint sameStepResourceConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Task.class)
                .filter(task -> task.getResourceItem().getId().equals(task.getRequiredResourceId()))
                .reward("sameStepResourceConflict",HardSoftScore.ofHard(100));
    }

    Constraint timeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ManufacturerOrder.class)
                .filter(order -> order.getPeriod().getEndTime().isBefore(order.getEndDate()))
                .penalize("time late", HardSoftScore.ofHard(1));
    }

    Constraint delayDaysConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ManufacturerOrder.class)
                .filter(manufacturerOrder -> manufacturerOrder.getType() == 0)
                .filter(order -> order.getEndDate() != null &&
                        order.getTotalDays() > order.getPeriod().getRequiredDuration())
                .penalize("time late", HardSoftScore.ofHard(1),
                        order -> order.getTotalDays() - order.getPeriod().getRequiredDuration());
    }

    Constraint differentLayerConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Task.class)
                .filter(task -> task.getUnit() == 1)
                .join(Task.class, Joiners.filtering((task1, task2) -> task1.getRelatedLayer().contains(task2.getLayerNum())))
                .join(Task.class, Joiners.filtering((task1, task2, task3) -> task2.getLayerNum() < task3.getLayerNum()))
                .penalize("Different layer conflict", HardSoftScore.ofHard(1));

    }

    Constraint sameLayerConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Layer.class, Joiners.equal(Layer::getProduct))
                .filter(((layer, layer2) -> layer.getLayerNumber() > layer2.getLayerNumber()))
                .penalize("One layer conflict", HardSoftScore.ofHard(5));
    }

    //Soft Constraint
    Constraint exchangeTimeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ResourceItem.class)
                .groupBy(ResourceItem::getCode, count())
                .penalize("min product change", HardSoftScore.ofSoft(1), (product, integer) -> integer);

    }

    Constraint priorityConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(ManufacturerOrder.class,
                        Joiners.lessThanOrEqual(ManufacturerOrder::getPriority))
                .reward("reward priority", HardSoftScore.ofSoft(1));

    }

}
