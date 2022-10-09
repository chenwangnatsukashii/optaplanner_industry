package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ScheduleDate implements Serializable {

    private LocalDateTime localDateTime;
    private Timeslot timeslot;

}
