package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@PlanningEntity
public class Task {

    @PlanningId
    private Long id;
    // 任务code
    private String code;
    // 任务产能
    private Integer speed;
    // 单位：0代表层，1代表套
    private Integer unit;
    // 工序顺序
    private String taskOrder;
    // 层
    private Integer layerNum;
    // unit为1且为叠片工序时传入，代表前置层数约束
    private List<Integer> relatedLayer;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    private Timeslot timeslot;
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private WorkGroup workGroup;

    public Task(Long id, String code, Integer speed, Integer unit, String taskOrder, Integer layerNum, List<Integer> relatedLayer) {
        this.id = id;
        this.code = code;
        this.speed = speed;
        this.unit = unit;
        this.taskOrder = taskOrder;
        this.layerNum = layerNum;
        this.relatedLayer = relatedLayer;
    }
}
