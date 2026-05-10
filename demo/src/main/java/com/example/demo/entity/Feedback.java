package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 反馈实体类
 * 对应数据库feedback表
 * 存储用户的意见反馈
 */
@Data
public class Feedback {
    /** 反馈ID（主键） */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 关联的订单ID（可选） */
    private Long bookingId;
    /** 反馈内容描述 */
    private String description;
    /** 状态: OPEN（待处理）, IN_PROGRESS（处理中）, RESOLVED（已解决） */
    private String status;
    /** 优先级: LOW（低）, MEDIUM（中）, HIGH（高） */
    private String priority;
    /** 管理员回复内容 */
    private String adminResponse;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 解决时间 */
    private LocalDateTime resolvedAt;
}
