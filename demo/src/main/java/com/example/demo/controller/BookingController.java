package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Booking;
import com.example.demo.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 处理租赁订单的创建、查询、支付、取消、延期等操作
 * 路径: /api/bookings/*
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 创建新订单（租车）
     * POST /api/bookings
     * 参数: scooterId, hireOption, startTime 等
     */
    @PostMapping
    public Result<Booking> create(@RequestBody Booking booking, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        booking.setUserId(userId);
        if (bookingService.save(booking)) {
            return Result.success(booking);
        }
        return Result.error("Failed to create booking");
    }

    /**
     * 获取当前用户的订单列表
     * GET /api/bookings
     */
    @GetMapping
    public Result<List<Booking>> findMyBookings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(bookingService.findByUserId(userId));
    }

    /**
     * 根据ID获取订单详情
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    public Result<Booking> findById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error("Booking not found");
    }

    /**
     * 延长租期
     * PUT /api/bookings/{id}/extend?hireOption=1day
     * 参数: hireOption - 延长的时长选项
     */
    @PutMapping("/{id}/extend")
    public Result<String> extend(@PathVariable Long id, @RequestParam String hireOption) {
        if (bookingService.extendBooking(id, hireOption)) {
            return Result.success("Booking extended successfully");
        }
        return Result.error("Failed to extend booking");
    }

    /**
     * 取消订单
     * POST /api/bookings/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        if (bookingService.cancelBooking(id)) {
            return Result.success("Booking cancelled successfully");
        }
        return Result.error("Failed to cancel booking");
    }

    /**
     * 支付订单
     * POST /api/bookings/{id}/pay
     */
    @PostMapping("/{id}/pay")
    public Result<String> pay(@PathVariable Long id) {
        if (bookingService.payBooking(id)) {
            return Result.success("Payment successful");
        }
        return Result.error("Payment failed");
    }

    /**
     * 获取订单确认信息（生成确认码等）
     * GET /api/bookings/{id}/confirmation
     */
    @GetMapping("/{id}/confirmation")
    public Result<Map<String, Object>> getConfirmation(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            return Result.error("Booking not found");
        }
        Map<String, Object> confirmation = new HashMap<>();
        confirmation.put("confirmationCode", booking.getConfirmationCode());
        confirmation.put("scooterId", booking.getScooterId());
        confirmation.put("hireOption", booking.getHireOption());
        confirmation.put("startTime", booking.getStartTime());
        confirmation.put("endTime", booking.getEndTime());
        confirmation.put("totalCost", booking.getTotalCost());
        confirmation.put("status", booking.getStatus());
        return Result.success(confirmation);
    }
}
