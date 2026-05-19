package com.example.demo.service;

import java.util.Map;

/**
 * 统计服务接口
 * 提供管理员后台所需的各类统计数据
 */
public interface StatisticsService {
    /**
     * 获取运营概览统计
     */
    Map<String, Object> getOverview();

    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStats();

    /**
     * 获取订单统计数据
     */
    Map<String, Object> getBookingStats();

    /**
     * 获取车辆统计数据
     */
    Map<String, Object> getScooterStats();

    /**
     * 获取每日收入趋势
     */
    Map<String, Object> getDailyIncome();

    /**
     * 获取每周收入统计
     */
    Map<String, Object> getWeeklyIncome();

    /**
     * 获取用户增长趋势
     */
    Map<String, Object> getUserGrowth();

    /**
     * 获取活跃用户 TOP 排行
     */
    Map<String, Object> getTopActiveUsers();

    /**
     * 获取订单状态分布
     */
    Map<String, Object> getBookingStatusDistribution();

    /**
     * 获取热门预订时段
     */
    Map<String, Object> getPeakHours();

    /**
     * ID20: 获取一周内每天的热门租赁日统计
     * 返回周一到周日的订单数量
     */
    Map<String, Object> getWeeklyHotDays();

    /**
     * ID20: 获取一周内每天各租期的收入详情
     * 返回每天的收入明细，包含1hr, 4hr, 1day, 1week各租期的收入
     */
    Map<String, Object> getDailyIncomeBreakdown();
}
