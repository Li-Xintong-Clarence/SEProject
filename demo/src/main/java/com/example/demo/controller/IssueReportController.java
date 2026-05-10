package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.IssueReport;
import com.example.demo.entity.User;
import com.example.demo.service.IssueReportService;
import com.example.demo.service.UserService;
import com.example.demo.vo.IssueReportRequest;
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
    @Autowired
    private UserService userService;

    /**
     * 创建问题报告（ID14：Report a problem）
     * 用户报告滑板车的问题（如故障、损坏等）
     * @param request 问题报告请求，包含问题描述和相关滑板车ID
     * @param token JWT令牌（格式：Bearer xxx）
     * @return 创建成功返回报告信息，失败返回错误信息
     */
    @PostMapping
    public Result<IssueReport> create(@RequestBody IssueReportRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("Unauthorized");
        }
        String username = token.replace("Bearer ", "");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        IssueReport report = issueReportService.create(user.getId(), request);
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
    public Result<List<IssueReport>> myIssues(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("Unauthorized");
        }
        String username = token.replace("Bearer ", "");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        return Result.success(issueReportService.findByUserId(user.getId()));
    }
}
