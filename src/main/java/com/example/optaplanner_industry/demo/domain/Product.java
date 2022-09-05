package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // 产品唯一ID
    private Integer id;
    // 产品编号
    private String code;
    // 产品名称
    private String name;

}
