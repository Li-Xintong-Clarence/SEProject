package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.IssueReport;
import com.example.demo.entity.Feedback;
import com.example.demo.service.IssueReportService;
import com.example.demo.vo.IssueReportRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 问题报告控制器
 * 处理问题报告相关的API请求（ID13/14：报告问题）
 * 包括创建问题报告、查询问题报告等操作
 */
@RestController
@RequestMapping("/api/issues")
@CrossOrigin
public class IssueReportController {

    private static final Logger log = LoggerFactory.getLogger(IssueReportController.class);

    @Autowired
    private IssueReportService issueReportService;

    /**
     * 创建问题报告（ID14：Report a problem）
     * 用户报告滑板车的问题（如故障、损坏等）
     * @param request 问题报告请求，包含问题描述和相关滑板车ID
     * @return 创建成功返回报告信息，失败返回错误信息
     */
    @PostMapping
    public Result<String> create(@RequestBody IssueReportRequest issueRequest,
                                       HttpServletRequest httpRequest) {
        log.info("Received issue report request: {}", issueRequest);
        Long userId = (Long) httpRequest.getAttribute("userId");
        log.info("User ID from token: {}", userId);
        if (userId == null) {
            log.warn("User ID is null, unauthorized");
            return Result.error("Unauthorized");
        }
        try {
            IssueReport report = issueReportService.create(userId, issueRequest);
            log.info("Issue report created successfully: {}", report);
            return Result.success("Issue report created successfully");
        } catch (Exception e) {
            log.error("Failed to create issue report", e);
            return Result.error("Failed to create issue report: " + e.getMessage());
        }
    }

    /**
     * 查询所有问题报告（管理员功能）
     * @return 所有问题报告列表
     */
    @GetMapping
    public Result<List<IssueReport>> findAll() {
        return Result.success(issueReportService.findAll());
    }

    /**
     * 查询高优先级问题报告（管理员功能 - ID15）
     * @return 高优先级问题报告列表
     */
    @GetMapping("/high-priority")
    public Result<List<IssueReport>> findHighPriority() {
        return Result.success(issueReportService.findByPriority("HIGH"));
    }

    /**
     * 查询当前登录用户提交的问题报告
     * 需要用户登录
     * @return 当前用户提交的所有问题报告列表
     */
    @GetMapping("/my")
    public Result<List<IssueReport>> myIssues(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("Unauthorized");
        }
        try {
            return Result.success(issueReportService.findByUserId(userId));
        } catch (Exception e) {
            return Result.error("Failed to get issue reports");
        }
    }

    /**
     * 更新问题报告状态（管理员处理）
     * @param id 问题报告ID
     * @param issue 更新请求，包含状态、优先级、管理员反馈
     * @return 更新成功返回成功信息
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody IssueReport issue) {
        IssueReport existing = issueReportService.findById(id);
        if (existing == null) {
            return Result.error("Issue report not found");
        }
        if (issue.getStatus() != null) {
            existing.setStatus(issue.getStatus());
        }
        if (issue.getPriority() != null) {
            existing.setPriority(issue.getPriority());
        }
        if (issue.getAdminFeedback() != null) {
            existing.setAdminFeedback(issue.getAdminFeedback());
        }
        if ("RESOLVED".equals(issue.getStatus())) {
            existing.setResolvedAt(LocalDateTime.now());
        }
        if (issueReportService.update(existing)) {
            return Result.success("Issue report updated successfully");
        }
        return Result.error("Failed to update issue report");
    }
}
