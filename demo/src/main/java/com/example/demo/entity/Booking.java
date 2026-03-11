package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预订实体类
 * 表示用户的滑板车租赁预订（ID5：预订滑板车）
 */
@Data
public class Booking {
    private Long id;
    private Long userId;
    private Long scooterId;
    private String hireOptionCode;
    private LocalDateTime startTime;
    private LocalDateTime scheduledEndTime;
    private LocalDateTime actualEndTime;
    private Integer actualDurationMinutes;
    private BigDecimal cost;
    private String status;         // 预订状态：ACTIVE（使用中）, COMPLETED（已完成）, CANCELLED（已取消）
    private LocalDateTime createdAt;
}
