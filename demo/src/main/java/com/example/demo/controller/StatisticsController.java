package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 统计控制器
 * 提供管理员后台所需的各类统计数据
 * 路径: /api/statistics/*
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取运营概览统计
     * GET /api/statistics/overview
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    /**
     * 获取用户统计数据
     * GET /api/statistics/users
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserStats() {
        return Result.success(statisticsService.getUserStats());
    }

    /**
     * 获取订单统计数据
     * GET /api/statistics/bookings
     */
    @GetMapping("/bookings")
    public Result<Map<String, Object>> getBookingStats() {
        return Result.success(statisticsService.getBookingStats());
    }

    /**
     * 获取车辆统计数据
     * GET /api/statistics/scooters
     */
    @GetMapping("/scooters")
    public Result<Map<String, Object>> getScooterStats() {
        return Result.success(statisticsService.getScooterStats());
    }

    /**
     * 获取每日收入趋势
     * GET /api/statistics/income/daily
     */
    @GetMapping("/income/daily")
    public Result<Map<String, Object>> getDailyIncome() {
        return Result.success(statisticsService.getDailyIncome());
    }

    /**
     * 获取每周收入统计
     * GET /api/statistics/income/weekly
     */
    @GetMapping("/income/weekly")
    public Result<Map<String, Object>> getWeeklyIncome() {
        return Result.success(statisticsService.getWeeklyIncome());
    }

    /**
     * 获取用户增长趋势（最近7天）
     * GET /api/statistics/users/growth
     */
    @GetMapping("/users/growth")
    public Result<Map<String, Object>> getUserGrowth() {
        return Result.success(statisticsService.getUserGrowth());
    }

    /**
     * 获取活跃用户 TOP 排行
     * GET /api/statistics/users/top
     */
    @GetMapping("/users/top")
    public Result<Map<String, Object>> getTopUsers() {
        return Result.success(statisticsService.getTopActiveUsers());
    }

    /**
     * 获取订单状态分布
     * GET /api/statistics/bookings/status
     */
    @GetMapping("/bookings/status")
    public Result<Map<String, Object>> getBookingStatusDistribution() {
        return Result.success(statisticsService.getBookingStatusDistribution());
    }

    /**
     * 获取热门预订时段
     * GET /api/statistics/bookings/peak-hours
     */
    @GetMapping("/bookings/peak-hours")
    public Result<Map<String, Object>> getPeakHours() {
        return Result.success(statisticsService.getPeakHours());
    }
}
