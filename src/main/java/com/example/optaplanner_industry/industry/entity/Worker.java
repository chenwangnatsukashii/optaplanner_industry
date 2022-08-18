package com.example.optaplanner_industry.industry.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Worker {

    private String workerName;

    @Override
    public String toString() {
        return workerName;
    }
}
