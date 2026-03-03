package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Scooter {
    private Long id;
    private String scooterNumber;    // 滑板车编号
    private String status;            // AVAILABLE, IN_USE, MAINTENANCE
    private BigDecimal batteryLevel;  // 电量
    private Double latitude;          // 纬度
    private Double longitude;         // 经度
    private String location;          // 位置描述
    private LocalDateTime lastMaintenanceDate;
}
