package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkGroup {

    private String name;

    @Override
    public String toString() {
        return name;
    }

}
