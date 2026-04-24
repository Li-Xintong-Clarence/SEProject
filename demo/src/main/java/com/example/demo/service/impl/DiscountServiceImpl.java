package com.example.demo.service.impl;

import com.example.demo.service.DiscountService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 折扣服务实现类
 * 实现学生和长者优惠逻辑
 */
@Service
public class DiscountServiceImpl implements DiscountService {

    /** 学生折扣：30% off（即付 70%） */
    private static final double STUDENT_DISCOUNT = 0.30;

    /** 长者折扣：50% off（即付 50%） */
    private static final double SENIOR_DISCOUNT = 0.50;

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
    public double calculateDiscountedPrice(double originalPrice, String userType) {
        double discountRate = getDiscountRate(userType);
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
}
