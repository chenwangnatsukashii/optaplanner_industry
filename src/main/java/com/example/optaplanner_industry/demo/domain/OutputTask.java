package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputTask implements Serializable {

    private String originalId;
    private Integer subId;
    private String code;
    private Integer speed;
    private Integer unit;
    private Integer layerNum;
    private Integer taskType;
    private List<Integer> relatedLayer;
    private Integer amount;
    private LocalDate runTime;
    private Integer schedule;
    private String stepId;

    @Override
    public String toString() {
        return "日期: " + this.runTime + " 任务code: " + this.code + " 班次: " + this.schedule + " 工序组: " +
                this.stepId + " 层数: " + this.layerNum + " 生产数: " + this.amount + " 产量速度: " + this.speed;
    }

}
