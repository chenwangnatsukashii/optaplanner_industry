package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Output implements Serializable {

    private Integer code;
    private String message;
    private int status;
    private String requestId;
    private List<OutputManufacturerOrder> manufacturerOrderList;

}
