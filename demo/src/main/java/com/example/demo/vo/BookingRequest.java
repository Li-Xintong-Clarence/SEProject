package com.example.demo.vo;

import lombok.Data;

/**
 * 预订请求类
 * 用于创建预订的请求参数（ID5：预订滑板车）
 */
@Data
public class BookingRequest {
    private Long scooterId;       // 要预订的滑板车ID
    private String hireOptionCode; // 租用选项代码：1HR, 4HR, 1DAY, 1WEEK
}
