package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 热门区域实体类
 * 表示滑板车的热点分布区域
 */
@Data
public class Hotspot {
    /** 区域ID */
    private Long id;
    /** 区域名称 */
    private String name;
    /** 纬度（中心点） */
    private Double latitude;
    /** 经度（中心点） */
    private Double longitude;
    /** 区域半径（米） */
    private Integer radius;
    /** 权重（0-100，权重越高分配越多滑板车） */
    private Integer weight;
    /** 状态：ACTIVE（活跃）、INACTIVE（不活跃） */
    private String status;
    /** 位置描述 */
    private String location;
}
