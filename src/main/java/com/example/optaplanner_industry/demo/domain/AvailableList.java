package com.example.optaplanner_industry.demo.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableList {

    private String id;
    private String code;
    private Period period;
    private Integer capacity;

}
