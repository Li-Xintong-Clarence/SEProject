package com.example.demo.vo;

import lombok.Data;

/**
 * 问题报告请求类
 * 用于提交问题报告的请求参数（ID13/14：报告问题）
 */
@Data
public class IssueReportRequest {
    private Long scooterId;      // 出现问题的滑板车ID
    private String description;  // 问题描述
}
