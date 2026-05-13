package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Feedback;
import com.example.demo.entity.IssueReport;
import com.example.demo.entity.Pricing;
import com.example.demo.entity.Scooter;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.BookingService;
import com.example.demo.service.DiscountService;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.IssueReportService;
import com.example.demo.service.PricingService;
import com.example.demo.service.ScooterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 处理管理员专属操作：订单管理、反馈处理、收入报表、价格配置等
 * ID7: 支持员工为未注册用户创建订单
 * 路径: /api/admin/*
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private ScooterService scooterService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private IssueReportService issueReportService;

    /**
     * 管理员代客预订（为用户或访客创建订单）
     * POST /api/admin/bookings
     * ID7: 支持已注册用户和未注册用户(guest)预订
     */
    @PostMapping("/bookings")
    public Result<Booking> createBookingForUser(@RequestBody Booking booking) {
        logger.info("=== Admin createBookingForUser called ===");
        logger.info("bookingType: {}, userId: {}, scooterId: {}, hireOption: {}",
                   booking.getBookingType(), booking.getUserId(), booking.getScooterId(), booking.getHireOption());
        logger.info("guestName: {}, guestPhone: {}, guestEmail: {}",
                   booking.getGuestName(), booking.getGuestPhone(), booking.getGuestEmail());

        // ID7: 根据预订类型验证
        boolean isGuest = "GUEST".equals(booking.getBookingType()) || booking.getUserId() == null;
        logger.info("isGuest: {}", isGuest);

        if (!isGuest && booking.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        // Guest 预订必须提供姓名和电话
        if (isGuest) {
            if (booking.getGuestName() == null || booking.getGuestName().trim().isEmpty()) {
                return Result.error("访客姓名不能为空");
            }
            if (booking.getGuestPhone() == null || booking.getGuestPhone().trim().isEmpty()) {
                return Result.error("访客电话不能为空");
            }
            booking.setBookingType("GUEST");
        } else {
            booking.setBookingType("REGISTERED");
        }

        if (booking.getScooterId() == null) {
            return Result.error("车辆ID不能为空");
        }
        if (booking.getHireOption() == null || booking.getHireOption().isEmpty()) {
            return Result.error("请选择租用时长");
        }

        // 如果没有指定取车服务点，自动从车辆获取
        if (booking.getStartDepotId() == null) {
            Scooter scooter = scooterService.findById(booking.getScooterId());
            if (scooter != null && scooter.getDepotId() != null) {
                booking.setStartDepotId(scooter.getDepotId());
                logger.info("Auto-set startDepotId from scooter: {}", scooter.getDepotId());
            }
        }

        // 管理员创建订单时不检查用户是否已有活跃订单（这是管理员的权限）
        // 但仍然需要计算价格
        com.example.demo.entity.Pricing pricing = pricingService.findByHireOption(booking.getHireOption());
        logger.info("Found pricing: {}", pricing);
        if (pricing != null) {
            // ID7: Guest 预订不享受折扣
            if (isGuest) {
                booking.setTotalCost(pricing.getPrice());
            } else {
                User bookingUser = userMapper.findById(booking.getUserId());
                double finalPrice = discountService.calculateDiscountedPrice(
                    pricing.getPrice().doubleValue(),
                    bookingUser != null ? bookingUser.getUserType() : "NORMAL"
                );
                booking.setTotalCost(java.math.BigDecimal.valueOf(finalPrice));
            }
            logger.info("Total cost set to: {}", booking.getTotalCost());
        } else {
            // 如果没有找到定价，设置默认值
            logger.warn("Pricing not found for option: {}, using default price", booking.getHireOption());
            booking.setTotalCost(java.math.BigDecimal.valueOf(10.0));
        }

        logger.info("About to call bookingService.adminSave...");
        if (bookingService.adminSave(booking)) {
            logger.info("Booking created successfully, ID: {}", booking.getId());
            return Result.success(booking);
        }
        logger.error("BookingService.adminSave returned false");
        return Result.error("订单创建失败，请检查数据是否完整或稍后重试");
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
     * 获取高优先级问题列表（ID14: 查看高优先级问题）
     * GET /api/admin/issues
     * 返回 IssueReport 实体列表，而非 Feedback
     */
    @GetMapping("/issues")
    public Result<List<IssueReport>> getHighPriorityIssues() {
        return Result.success(issueReportService.findByPriority("HIGH"));
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
