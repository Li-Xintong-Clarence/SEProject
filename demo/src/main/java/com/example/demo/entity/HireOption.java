package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 租用选项实体类
 * 表示滑板车租赁的时间选项和价格（ID4：查看租用选项和费用）
 */
@Data
public class HireOption {
    private Long id;
    private String code;           // 租用选项代码：1HR, 4HR, 1DAY, 1WEEK
    private String label;           // 显示标签，如"1小时"
    private Integer durationMinutes;  // 时长（分钟）
    private BigDecimal price;      // 价格
    private Integer displayOrder;  // 显示顺序
    private Boolean isActive;      // 是否激活
}
