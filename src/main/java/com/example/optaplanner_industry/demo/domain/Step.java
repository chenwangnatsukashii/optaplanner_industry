package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Step {

    // 步骤ID
    private Integer id;
    // 步骤编号
    private String code;
    // 步骤名称
    private String name;

}
