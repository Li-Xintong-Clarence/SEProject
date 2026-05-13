package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单（租赁）实体类
 * 对应数据库booking表
 * 表示用户的电动车租赁订单
 * 支持已注册用户和未注册用户(guest)预订
 */
@Data
public class Booking {
    /** 订单ID（主键） */
    private Long id;
    /** 用户ID（已注册用户） */
    private Long userId;
    /** 电动车ID */
    private Long scooterId;
    /** 租赁选项: 1hr（1小时）, 4hr（4小时）, 1day（1天）, 1week（1周） */
    private String hireOption;
    /** 租赁开始时间 */
    private LocalDateTime startTime;
    /** 租赁结束时间 */
    private LocalDateTime endTime;
    /** 总费用 */
    private BigDecimal totalCost;
    /** 订单状态: PENDING（待支付）, PAID（已支付）, ACTIVE（进行中）, COMPLETED（已完成）, CANCELLED（已取消） */
    private String status;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 预订确认码（8位随机字符串） */
    private String confirmationCode;
    /** 取车服务点ID */
    private Long startDepotId;
    /** 还车服务点ID */
    private Long endDepotId;

    // === ID7: 未注册用户(guest)预订支持 ===
    /** 预订类型: REGISTERED(已注册用户) / GUEST(未注册用户) */
    private String bookingType;
    /** 访客姓名（未注册用户必填） */
    private String guestName;
    /** 访客电话（未注册用户必填） */
    private String guestPhone;
    /** 访客邮箱（可选） */
    private String guestEmail;

    /**
     * 判断是否为访客预订
     */
    public boolean isGuestBooking() {
        return "GUEST".equals(bookingType) || userId == null;
    }
}
