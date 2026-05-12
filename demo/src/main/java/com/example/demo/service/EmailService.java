package com.example.demo.service;

/**
 * 邮件服务接口
 * 定义发送邮件的相关操作
 */
public interface EmailService {
    /**
     * 发送预订确认邮??
     * @param toEmail 收件人邮??
     * @param username 用户??
     * @param confirmationCode 预订确认??
     * @param scooterNumber 电动车编??
     * @param hireOption 租赁选项
     * @param startTime 开始时??
     * @param endTime 结束时间
     * @param totalCost 总费??
     */
    void sendBookingConfirmation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption, String startTime, String endTime, double totalCost);

    /**
     * 发送订单取消邮??
     */
    void sendBookingCancellation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption);

    /**
     * 发送结束骑行邮??
     */
    void sendRideCompletion(String toEmail, String username, String confirmationCode,
                           String scooterNumber, String startTime, String endTime, double totalCost);

    /**
     * 发送自动还车通知邮件
     * @param toEmail 收件人邮??
     * @param username 用户??
     * @param confirmationCode 预订确认??
     * @param scooterNumber 电动车编??
     * @param startTime 开始时??
     * @param endTime 结束时间
     * @param depotName 还车服务点名??
     * @param totalCost 总费??
     */
    void sendAutoReturnNotification(String toEmail, String username, String confirmationCode,
                                    String scooterNumber, String startTime, String endTime,
                                    String depotName, double totalCost);
}
