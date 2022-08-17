package com.example.optaplanner_industry.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Room {

    private String name;

    @Override
    public String toString() {
        return name;
    }

}
