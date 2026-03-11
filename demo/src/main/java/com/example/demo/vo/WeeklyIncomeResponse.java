package com.example.demo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 周收入响应类
 * 用于返回按租用选项分组的周收入统计数据（ID19：按租用选项查看周收入）
 */
@Data
public class WeeklyIncomeResponse {
    private String startDate;                      // 统计开始日期
    private String endDate;                        // 统计结束日期
    private Map<String, BigDecimal> incomeByOption;  // 按租用选项统计的收入，key为选项代码如"1HR"
    private BigDecimal totalIncome;                // 总收入
}
