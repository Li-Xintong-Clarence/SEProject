package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 超时费用配置实体类
 * 配置超过租赁时长后的超时费用
 */
@Data
public class OvertimeFee {
    /** 配置ID（主键） */
    private Long id;
    /** 关联的租期选项代码（如 1hr, 4hr, 1day, 1week） */
    private String hireOption;
    /** 租期描述 */
    private String hireOptionName;
    /** 超时费率类型：HOURLY（按小时）, FIXED（固定金额/超时） */
    private String feeType;
    /** 超时费用（按小时或固定） */
    private BigDecimal fee;
    /** 最大超时限制（分钟），NULL表示不限制 */
    private Integer maxOvertimeMinutes;
    /** 是否启用 */
    private Boolean enabled;
}
