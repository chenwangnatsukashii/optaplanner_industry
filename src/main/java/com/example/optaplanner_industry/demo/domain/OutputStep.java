package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputStep implements Serializable {

    private String id; // 步骤ID
    private String code; // 步骤编号
    private String name; // 步骤名称
    private LocalDate stepStartTime; // 整个工序开始时间
    private long executionDays; // 整个工序开始时间
    private List<ResourceRequirement> resourceRequirementList; // 所需资源列表
    private List<OutputTask> taskList; // 任务列表

}
