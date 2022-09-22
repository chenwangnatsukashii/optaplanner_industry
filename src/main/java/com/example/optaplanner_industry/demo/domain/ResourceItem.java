package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceItem {
    private String id;

    private String code;

    private Period period;

    private Integer capacity;
}
