package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 问题报告实体类
 * 表示用户报告的滑板车问题（ID13/14：报告问题）
 */
@Data
public class IssueReport {
    private Long id;
    private Long userId;
    private Long scooterId;
    private String description;
    private String status;        // 问题状态：PENDING（待处理）, IN_PROGRESS（处理中）, RESOLVED（已解决）
    private String priority;      // 优先级：LOW（低）, NORMAL（普通）, HIGH（高）
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String adminFeedback;
}
