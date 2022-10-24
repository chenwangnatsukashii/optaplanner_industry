package com.example.optaplanner_industry.demo.domain;

import com.example.optaplanner_industry.demo.solver.StartTimeUpdatingVariableListener;
import lombok.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
public class Task extends TaskOrResource implements Comparable<Task>, Serializable {

    @PlanningId
    private String id;
    private String code; // 任务code
    private Integer speed; // 任务产能
    private Integer unit; // 单位：0代表层，1代表套
    private Integer layerNum; // 层
    private List<Integer> relatedLayer; // unit为1且为叠片工序时传入，代表前置层数约束
    private int readyTaskNum; // 完成的前置任务数量

    @PlanningVariable(valueRangeProviderRefs = {"resourceRange", "taskRange"}, graphType = PlanningVariableGraphType.CHAINED)
    private TaskOrResource previousTaskOrResource;

    private Task preTask;

    @AnchorShadowVariable(sourceVariableName = "previousTaskOrResource")
    private ResourceItem resourceItem;
    @CustomShadowVariable(variableListenerClass = StartTimeUpdatingVariableListener.class,
            sources = {@PlanningVariableReference(variableName = "previousTaskOrResource")})
    private Integer startTime; // 开始时间

    @PlanningVariable(valueRangeProviderRefs = "scheduleRange")
    private ScheduleDate scheduleDate;

    private Integer endTime; // 结束时间
    private LocalDateTime actualStartTime; // 实际开始时间
    private LocalDateTime actualEndTime; // 实际结束时间
    private int readyTime; // 任务准备时间
    private String requiredResourceId; // 任务所需资源ID
    private Integer schedule; // 班次
    private Integer amount; // 加工的数量
    private LocalDate runTime; // 加工当天日期
    private String productId; // 产品ID
    private String stepId; // 工序ID
    private Integer stepIndex;
    private String manufactureOrderId; // 工单ID
    private Integer quantity; // 工单生产个数
    private LocalDateTime taskBeginTime; // 工单开始时间

    private Product product;
    private String orderId;
    private Integer duration;
    private BigDecimal singleTimeSlotSpeed;
    private BigDecimal timeSlotDuration;
    private Integer minutesDuration;
    private ManufacturerOrder manufacturerOrder;

    public String getFullTaskName() {
        return this.code + " 所在工序组：" + Optional.ofNullable(this.requiredResourceId).orElse("错误：工序组为空")
                + " 开始时间：" + taskBeginTime.plusMinutes(Optional.ofNullable(this.startTime).orElse(0)) +
                " 结束时间：" + taskBeginTime.plusMinutes(Optional.ofNullable(this.endTime).orElse(0));
    }

    @Override
    public Integer getEndTime(int quantity) {
        if (startTime == null) {
            return null;
        }

        this.endTime = startTime + (int) Math.ceil(24.0 * 60 * quantity / this.speed);

        this.actualStartTime = this.taskBeginTime.plusMinutes(Optional.of(this.startTime).orElse(0));
        this.actualEndTime = taskBeginTime.plusMinutes(Optional.of(this.endTime).orElse(0));

        return this.endTime;
    }


    @Override
    public int compareTo(Task task) {
        return this.getStepIndex() - task.getStepIndex();
    }
}
