package com.example.demo.service;

import com.example.demo.entity.User;

/**
 * 折扣服务接口
 * 定义折扣相关的业务方法
 */
public interface DiscountService {

    /**
     * 计算折扣比例
     * @param userType 用户类型：STUDENT=学生，SENIOR=长者，NORMAL=普通
     * @return 折扣比例（0.0-1.0），如 0.30 表示 7 折
     */
    double getDiscountRate(String userType);

    /**
     * 计算折扣比例（含频繁用户）
     * @param user 用户对象
     * @return 折扣比例（0.0-1.0）
     */
    double getDiscountRateForUser(User user);

    /**
     * 计算折后价格
     * @param originalPrice 原始价格
     * @param userType 用户类型
     * @return 折后价格
     */
    double calculateDiscountedPrice(double originalPrice, String userType);

    /**
     * 计算折后价格（含频繁用户折扣）
     * @param originalPrice 原始价格
     * @param user 用户对象
     * @return 折后价格
     */
    double calculateDiscountedPriceForUser(double originalPrice, User user);

    /**
     * 获取用户类型描述
     * @param userType 用户类型
     * @return 中文描述
     */
    String getUserTypeDescription(String userType);

    /**
     * 获取用户折扣描述
     * @param user 用户对象
     * @return 折扣描述
     */
    String getDiscountDescription(User user);
}
