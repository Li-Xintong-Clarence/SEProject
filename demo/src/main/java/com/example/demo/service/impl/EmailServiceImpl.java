package com.example.demo.service.impl;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.mail.username:}")
    private String fromEmail;

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
        message.setFrom(fromEmail);
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
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendBookingCancellation(String toEmail, String username, String confirmationCode,
                                       String scooterNumber, String hireOption) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("预订已取消 - Scooter Rental");
        String text = "亲爱的 " + username + "，\n\n" +
            "您的电动车预订已被取消。\n\n" +
            "确认码: " + confirmationCode + "\n" +
            "车辆编号: " + scooterNumber + "\n" +
            "租赁时长: " + hireOption + "\n\n" +
            "如需重新预订，请访问我们的网站。\n\n" +
            "如有疑问，请联系我们。\n" +
            "Scooter Rental Team";
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendRideCompletion(String toEmail, String username, String confirmationCode,
                                  String scooterNumber, String startTime, String endTime, double totalCost) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("骑行结束 - Scooter Rental");
        String text = "亲爱的 " + username + "，\n\n" +
            "感谢您使用 Scooter Rental！\n\n" +
            "您的骑行已结束。\n\n" +
            "确认码: " + confirmationCode + "\n" +
            "车辆编号: " + scooterNumber + "\n" +
            "开始时间: " + startTime + "\n" +
            "结束时间: " + endTime + "\n" +
            "总费用: £" + String.format("%.2f", totalCost) + "\n\n" +
            "请将车辆停放在指定区域并锁好。\n\n" +
            "期待再次为您服务！\n" +
            "Scooter Rental Team";
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
