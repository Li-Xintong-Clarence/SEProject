package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Feedback;
import com.example.demo.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 意见反馈控制器
 * 处理用户提交反馈、查看反馈等操作
 * 路径: /api/feedback/*
 */
@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 获取所有反馈（管理员）
     * GET /api/feedback
     */
    @GetMapping
    public Result<List<Feedback>> findAll() {
        return Result.success(feedbackService.findAll());
    }

    /**
     * 获取当前用户的反馈列表
     * GET /api/feedback/my
     */
    @GetMapping("/my")
    public Result<List<Feedback>> findMyFeedbacks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(feedbackService.findByUserId(userId));
    }

    /**
     * 根据ID获取反馈详情
     * GET /api/feedback/{id}
     */
    @GetMapping("/{id}")
    public Result<Feedback> findById(@PathVariable Long id) {
        Feedback feedback = feedbackService.findById(id);
        if (feedback != null) {
            return Result.success(feedback);
        }
        return Result.error("Feedback not found");
    }

    /**
     * 提交新反馈
     * POST /api/feedback
     * 参数: title, content, priority
     */
    @PostMapping
    public Result<String> create(@RequestBody Feedback feedback, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        feedback.setUserId(userId);
        if (feedbackService.save(feedback)) {
            return Result.success("Feedback submitted successfully");
        }
        return Result.error("Failed to submit feedback");
    }

    /**
     * 更新反馈
     * PUT /api/feedback/{id}
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Feedback feedback) {
        feedback.setId(id);
        if (feedbackService.update(feedback)) {
            return Result.success("Feedback updated successfully");
        }
        return Result.error("Failed to update feedback");
    }

    /**
     * 删除反馈
     * DELETE /api/feedback/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (feedbackService.deleteById(id)) {
            return Result.success("Feedback deleted successfully");
        }
        return Result.error("Failed to delete feedback");
    }
}
