package com.example.demo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 日收入响应类
 * 用于返回每日收入统计数据（ID20：按天查看收入）
 */
@Data
public class DailyIncomeResponse {
    private String startDate;                      // 统计开始日期
    private String endDate;                        // 统计结束日期
    private Map<String, BigDecimal> dailyIncome;  // 每日收入，key为日期字符串如"2026-03-01"
    private BigDecimal totalIncome;                // 总收入
}
