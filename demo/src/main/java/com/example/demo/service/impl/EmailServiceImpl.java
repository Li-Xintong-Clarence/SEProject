package com.example.demo.service.impl;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 使用Spring Mail发送预订确认邮件
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送预订确认邮件
     * 包含：确认码、车辆编号、租赁时长、时间、总费用等信息
     * 邮件发送失败不影响业务流程，只记录日志
     */
    @Override
    public void sendBookingConfirmation(String toEmail, String username, String confirmationCode,
                                        String scooterNumber, String hireOption,
                                        String startTime, String endTime, double totalCost) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("预订确认 - Scooter Rental");
        String text = "亲爱的 " + username + "，\n\n" +
            "您的电动车预订已确认！\n\n" +
            "确认码: " + confirmationCode + "\n" +
            "车辆编号: " + scooterNumber + "\n" +
            "租赁时长: " + hireOption + "\n" +
            "开始时间: " + startTime + "\n" +
            "结束时间: " + endTime + "\n" +
            "总费用: £" + String.format("%.2f", totalCost) + "\n\n" +
            "请在取车时出示此确认码。\n\n" +
            "祝您使用愉快！\n" +
            "Scooter Rental Team";
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // 邮件发送失败不影响业务流程，只记录日志
            System.err.println("邮件发送失败: " + e.getMessage());
        }
    }
}
