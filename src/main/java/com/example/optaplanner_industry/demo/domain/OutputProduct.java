package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputProduct implements Serializable {

    private String id; // 产品唯一ID
    private String code; // 产品编号
    private String name; // 产品名称
    private List<OutputStep> stepList; // 当前产品的生产步骤列表

}
