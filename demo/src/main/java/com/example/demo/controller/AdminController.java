package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Feedback;
import com.example.demo.entity.Pricing;
import com.example.demo.service.BookingService;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 处理管理员专属操作：订单管理、反馈处理、收入报表、价格配置等
 * 路径: /api/admin/*
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private PricingService pricingService;

    /**
     * 管理员代客预订（为用户创建订单）
     * POST /api/admin/bookings
     */
    @PostMapping("/bookings")
    public Result<Booking> createBookingForUser(@RequestBody Booking booking) {
        if (bookingService.save(booking)) {
            return Result.success(booking);
        }
        return Result.error("Failed to create booking");
    }

    /**
     * 获取所有用户反馈
     * GET /api/admin/feedback
     * 可选参数: priority=HIGH/MEDIUM/LOW
     */
    @GetMapping("/feedback")
    public Result<List<Feedback>> getAllFeedbacks(@RequestParam(required = false) String priority) {
        if (priority != null && !priority.isEmpty()) {
            return Result.success(feedbackService.findByPriority(priority));
        }
        return Result.success(feedbackService.findAll());
    }

    /**
     * 获取高优先级问题列表
     * GET /api/admin/issues
     */
    @GetMapping("/issues")
    public Result<List<Feedback>> getHighPriorityIssues() {
        return Result.success(feedbackService.findByPriority("HIGH"));
    }

    /**
     * 处理用户反馈（管理员回复）
     * PUT /api/admin/feedback/{id}
     * 参数: status, priority, adminResponse
     */
    @PutMapping("/feedback/{id}")
    public Result<String> processFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        Feedback existing = feedbackService.findById(id);
        if (existing == null) {
            return Result.error("Feedback not found");
        }
        existing.setStatus(feedback.getStatus());
        existing.setPriority(feedback.getPriority());
        existing.setAdminResponse(feedback.getAdminResponse());
        if ("RESOLVED".equals(feedback.getStatus())) {
            existing.setResolvedAt(java.time.LocalDateTime.now());
        }
        if (feedbackService.update(existing)) {
            return Result.success("Feedback processed successfully");
        }
        return Result.error("Failed to process feedback");
    }

    /**
     * 按租期类型统计周收入
     * GET /api/admin/reports/income/weekly
     * 返回: { "incomeByHireOption": {"1hr": 100, "4hr": 200, ...}, "totalIncome": 500 }
     */
    @GetMapping("/reports/income/weekly")
    public Result<Map<String, Object>> getWeeklyIncomeByHireOption() {
        List<Booking> allBookings = bookingService.findAll();
        Map<String, Double> incomeByOption = new HashMap<>();
        incomeByOption.put("1hr", 0.0);
        incomeByOption.put("4hr", 0.0);
        incomeByOption.put("1day", 0.0);
        incomeByOption.put("1week", 0.0);

        for (Booking b : allBookings) {
            if ("PAID".equals(b.getStatus()) || "COMPLETED".equals(b.getStatus())) {
                String option = b.getHireOption();
                double current = incomeByOption.getOrDefault(option, 0.0);
                incomeByOption.put(option, current + b.getTotalCost().doubleValue());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("incomeByHireOption", incomeByOption);
        result.put("totalIncome", incomeByOption.values().stream().mapToDouble(Double::doubleValue).sum());
        return Result.success(result);
    }

    /**
     * 按天汇总一周收入
     * GET /api/admin/reports/income/daily
     * 返回: { "dailyIncome": {"2026-03-10": 100, "2026-03-11": 150, ...}, "totalIncome": 500 }
     */
    @GetMapping("/reports/income/daily")
    public Result<Map<String, Object>> getDailyIncome() {
        List<Booking> allBookings = bookingService.findAll();
        Map<String, Double> dailyIncome = new HashMap<>();

        for (Booking b : allBookings) {
            if ("PAID".equals(b.getStatus()) || "COMPLETED".equals(b.getStatus())) {
                String day = b.getCreatedAt().toLocalDate().toString();
                double current = dailyIncome.getOrDefault(day, 0.0);
                dailyIncome.put(day, current + b.getTotalCost().doubleValue());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dailyIncome", dailyIncome);
        result.put("totalIncome", dailyIncome.values().stream().mapToDouble(Double::doubleValue).sum());
        return Result.success(result);
    }

    /**
     * 获取所有价格配置
     * GET /api/admin/pricing
     */
    @GetMapping("/pricing")
    public Result<List<Pricing>> getAllPricing() {
        return Result.success(pricingService.findAll());
    }

    /**
     * 更新价格配置
     * PUT /api/admin/pricing
     */
    @PutMapping("/pricing")
    public Result<String> updatePricing(@RequestBody Pricing pricing) {
        if (pricingService.update(pricing)) {
            return Result.success("Pricing updated successfully");
        }
        return Result.error("Failed to update pricing");
    }
}
