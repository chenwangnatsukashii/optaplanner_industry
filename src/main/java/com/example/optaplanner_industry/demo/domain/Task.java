package com.example.optaplanner_industry.demo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@PlanningEntity
public class Task {

    @PlanningId
    private String id;
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

    @PlanningVariable(valueRangeProviderRefs = "scheduleRange")
    private ScheduleDate scheduleDate;
    @PlanningVariable(valueRangeProviderRefs = "resourceRange")
    private ResourceItem resourceItem;

    private String requiredResourceId;
    //班次
    private Integer schedule;
    //安排好的时间
    private LocalDateTime time;
    //加工的数量
    private Integer amount;

    private String productId;

    private String stepId;

    private Integer stepIndex;

    public Task(String id, String code, Integer speed, Integer unit, String taskOrder, Integer layerNum, List<Integer> relatedLayer) {
        this.id = id;
        this.code = code;
        this.speed = speed;
        this.unit = unit;
        this.taskOrder = taskOrder;
        this.layerNum = layerNum;
        this.relatedLayer = relatedLayer;
    }
}
