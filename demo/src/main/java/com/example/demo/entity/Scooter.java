package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 滑板车实体类
 * 表示共享电动滑板车的基本信息
 */
@Data
public class Scooter {
    private Long id;
    private String scooterNumber;    // 滑板车编号
    private String status;            // 状态：AVAILABLE（可用）, IN_USE（使用中）, MAINTENANCE（维护中）
    private BigDecimal batteryLevel;  // 电量百分比
    private Double latitude;          // 纬度坐标
    private Double longitude;        // 经度坐标
    private String location;         // 位置描述
    private LocalDateTime lastMaintenanceDate;  // 上次维护日期
}
