package com.example.demo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预订响应类
 * 用于返回预订信息的响应数据（ID5：预订滑板车）
 */
@Data
public class BookingResponse {
    private Long bookingId;
    private String scooterNumber;
    private String hireOptionLabel;
    private Integer durationMinutes;
    private BigDecimal cost;
    private LocalDateTime startTime;
    private LocalDateTime scheduledEndTime;
    private String status;
}
