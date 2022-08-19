package com.example.optaplanner_industry.industry.domain;

import lombok.Data;

import java.util.Arrays;

@Data
public abstract class Resource {

    public Long resourceId;
    public String resourceCode;
    public Integer[] resourceType;

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId=" + resourceId +
                ", resourceCode='" + resourceCode + '\'' +
                ", resourceType=" + Arrays.toString(resourceType) +
                '}';
    }
}
