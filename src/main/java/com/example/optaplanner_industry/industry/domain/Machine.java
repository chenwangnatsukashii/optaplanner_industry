package com.example.optaplanner_industry.industry.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Machine {

    private String machineName;

    @Override
    public String toString() {
        return machineName;
    }
}