package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 价格配置实体类
 * 对应数据库pricing表
 * 定义不同租赁选项的价格
 */
@Data
public class Pricing {
    /** 价格配置ID（主键） */
    private Long id;
    /** 租赁选项: 1hr（1小时）, 4hr（4小时）, 1day（1天）, 1week（1周） */
    private String hireOption;
    /** 价格 */
    private BigDecimal price;
    /** 描述（如"1小时租赁"） */
    private String description;
}
