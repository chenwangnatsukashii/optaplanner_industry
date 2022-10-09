package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Period implements Serializable {

    private LocalDateTime startTime; // 生产计划就绪时间
    private LocalDateTime endTime; // 生产计划要求完成时间
    private Integer requiredDuration; // 持续时间

}
