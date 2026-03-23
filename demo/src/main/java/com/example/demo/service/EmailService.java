package com.example.demo.service;

/**
 * 邮件服务接口
 * 定义发送邮件的相关操作
 */
public interface EmailService {
    /**
     * 发送预订确认邮件
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param confirmationCode 预订确认码
     * @param scooterNumber 电动车编号
     * @param hireOption 租赁选项
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param totalCost 总费用
     */
    void sendBookingConfirmation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption, String startTime, String endTime, double totalCost);
}
