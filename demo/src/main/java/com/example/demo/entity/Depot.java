package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 服务点实体类
 * 对应数据库depot表
 * 表示滑板车租赁的服务站点
 */
@Data
public class Depot {
    /** 服务点ID（主键） */
    private Long id;
    /** 服务点编号（如"D001"） */
    private String depotNumber;
    /** 服务点名称（如"地铁站A口服务点"） */
    private String name;
    /** 纬度 */
    private BigDecimal latitude;
    /** 经度 */
    private BigDecimal longitude;
    /** 地址描述 */
    private String address;
    /** 容量（最大停放车辆数） */
    private Integer capacity;
    /** 当前库存（当前停放车辆数） */
    private Integer currentStock;
    /** 状态：ACTIVE（正常）、INACTIVE（停用） */
    private String status;
}
