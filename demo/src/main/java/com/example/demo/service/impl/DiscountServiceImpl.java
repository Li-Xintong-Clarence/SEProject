package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 折扣服务实现类
 * 实现学生、长者和频繁用户优惠逻辑
 */
@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private BookingMapper bookingMapper;

    /** 学生折扣：30% off（即付 70%） */
    private static final double STUDENT_DISCOUNT = 0.30;

    /** 长者折扣：50% off（即付 50%） */
    private static final double SENIOR_DISCOUNT = 0.50;

    /** 频繁用户折扣（每周8+小时）：20% off */
    private static final double FREQUENT_USER_DISCOUNT = 0.20;

    @Override
    public double getDiscountRate(String userType) {
        if (userType == null) {
            return 0.0;
        }
        return switch (userType.toUpperCase()) {
            case "STUDENT" -> STUDENT_DISCOUNT;
            case "SENIOR" -> SENIOR_DISCOUNT;
            default -> 0.0;
        };
    }

    @Override
    public double getDiscountRateForUser(User user) {
        if (user == null) {
            return 0.0;
        }
        // 优先使用用户类型折扣
        double userTypeDiscount = getDiscountRate(user.getUserType());

        // 检查频繁用户折扣（每周8+小时）
        double frequentDiscount = 0.0;
        if (user.getId() != null) {
            Integer weeklyHours = bookingMapper.getUserWeeklyHours(user.getId());
            if (weeklyHours != null && weeklyHours >= 8) {
                frequentDiscount = FREQUENT_USER_DISCOUNT;
            }
        }

        // 返回最高折扣（取较大值）
        return Math.max(userTypeDiscount, frequentDiscount);
    }

    @Override
    public double calculateDiscountedPrice(double originalPrice, String userType) {
        double discountRate = getDiscountRate(userType);
        double discountedPrice = originalPrice * (1.0 - discountRate);
        return BigDecimal.valueOf(discountedPrice)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public double calculateDiscountedPriceForUser(double originalPrice, User user) {
        double discountRate = getDiscountRateForUser(user);
        double discountedPrice = originalPrice * (1.0 - discountRate);
        return BigDecimal.valueOf(discountedPrice)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public String getUserTypeDescription(String userType) {
        if (userType == null) {
            return "普通用户";
        }
        return switch (userType.toUpperCase()) {
            case "STUDENT" -> "学生 (30% off)";
            case "SENIOR" -> "长者 (50% off)";
            default -> "普通用户 (无折扣)";
        };
    }

    @Override
    public String getDiscountDescription(User user) {
        if (user == null) {
            return "普通用户 (无折扣)";
        }

        // 检查频繁用户
        if (user.getId() != null) {
            Integer weeklyHours = bookingMapper.getUserWeeklyHours(user.getId());
            boolean isFrequent = weeklyHours != null && weeklyHours >= 8;

            // 如果是学生或长者
            String userType = user.getUserType();
            if ("STUDENT".equalsIgnoreCase(userType)) {
                return isFrequent ? "学生 + 频繁用户 (30% + 20% off)" : "学生 (30% off)";
            } else if ("SENIOR".equalsIgnoreCase(userType)) {
                return isFrequent ? "长者 + 频繁用户 (50% + 20% off)" : "长者 (50% off)";
            } else if (isFrequent) {
                return "频繁用户 (20% off)";
            }
        }

        return getUserTypeDescription(user.getUserType());
    }
}
