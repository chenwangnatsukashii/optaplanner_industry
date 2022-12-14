package com.example.optaplanner_industry.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerOrder implements Serializable {


    @PlanningId
    private String id; // 生产计划ID
    private String code; // 生产计划编号
    private String name; // 生产计划名称
    private Integer quantity; // 生产数量
    private Integer priority; // 优先级
    private Integer type; // 类型，生产计划类型：0代表正常生产计划，1代表样品生产计划
    private String relatedManufactureOrderId;  // type为1时传入，是为关联的正常生产计划ID
    private Integer delayDays; // type为0时传入，是为因打样导致的延滞天数
    private Period period; // 生产计划的就绪时间与要求完成日期
    // 当前生产计划对应的产品，目前的结构设计中，一个生产计划只对应一个产品。未来的版本将会视需求情况，可支持多个产品。
    // 因为产品与工艺信息相关，因此，即命名一个生产计划可对应多个产品，这些产品的工艺路线与资源需求，
    // 也需要一定的限制（工艺路线需要相同才有可能放在一个生产计划）。
    private Product product;
    private LocalDateTime endDate; // 最晚结束时间
    private Integer duration;  // 排程天数

    // 排程天数+延迟时间
    public Integer getTotalDays() {
        if (endDate == null) {
            return null;
        }
        return duration + delayDays;
    }


}
