package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.IssueReport;
import com.example.demo.service.IssueReportService;
import com.example.demo.vo.IssueReportRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 问题报告控制器
 * 处理问题报告相关的API请求（ID13/14：报告问题）
 * 包括创建问题报告、查询问题报告等操作
 */
@RestController
@RequestMapping("/api/issues")
@CrossOrigin
public class IssueReportController {

    @Autowired
    private IssueReportService issueReportService;

    /**
     * 创建问题报告（ID14：Report a problem）
     * 用户报告滑板车的问题（如故障、损坏等）
     * 用户身份由 JwtInterceptor 解析后写入 request attribute：userId
     */
    @PostMapping
    public Result<IssueReport> create(@RequestBody IssueReportRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("Unauthorized");
        }
        IssueReport report = issueReportService.create(userId, request);
        return Result.success(report);
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
     * 查询当前登录用户提交的问题报告
     * 需要用户登录
     * @param token JWT令牌（格式：Bearer xxx）
     * @return 当前用户提交的所有问题报告列表
     */
    @GetMapping("/my")
    public Result<List<IssueReport>> myIssues(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("Unauthorized");
        }
        return Result.success(issueReportService.findByUserId(userId));
    }
}
