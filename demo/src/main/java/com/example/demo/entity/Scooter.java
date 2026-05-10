package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 电动车实体类
 * 对应数据库scooters表
 * 表示共享电动车/滑板车
 */
@Data
public class Scooter {
    /** 电动车ID（主键） */
    private Long id;
    /** 滑板车编号（唯一标识，如"S001"） */
    private String scooterNumber;
    /** 状态: AVAILABLE（可用）, IN_USE（使用中）, MAINTENANCE（维护中）, RETIRED（已退役） */
    private String status;
    /** 电量（0-100） */
    private BigDecimal batteryLevel;
    /** 纬度（用于地图定位） */
    private Double latitude;
    /** 经度（用于地图定位） */
    private Double longitude;
    /** 位置描述（如"地铁站A口"） */
    private String location;
    /** 最后维护时间 */
    private LocalDateTime lastMaintenanceDate;
}
