package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerOrder {

    // 生产计划ID
    private Integer id;
    // 生产计划编号
    private String code;
    // 生产计划名称
    private String name;
    // 生产数量
    private Integer quantity;
    // 优先级
    private Integer priority;
    // 类型，生产计划类型：0代表正常生产计划，1代表样品生产计划
    private Integer type;
    // type为1时传入，是为关联的正常生产计划ID
    private Integer relatedManufactureOrderId;
    // type为0时传入，是为因打样导致的延滞天数
    private Integer delayDays;
    // 生产计划就绪时间
    private LocalDateTime startTime;
    // 生产计划要求完成时间
    private LocalDateTime endTime;


}
