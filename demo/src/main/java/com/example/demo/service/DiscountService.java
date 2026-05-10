package com.example.demo.service;

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
     * 计算折后价格
     * @param originalPrice 原始价格
     * @param userType 用户类型
     * @return 折后价格
     */
    double calculateDiscountedPrice(double originalPrice, String userType);

    /**
     * 获取用户类型描述
     * @param userType 用户类型
     * @return 中文描述
     */
    String getUserTypeDescription(String userType);
}
