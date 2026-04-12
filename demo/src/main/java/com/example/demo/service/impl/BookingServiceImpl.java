package com.example.demo.service.impl;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Pricing;
import com.example.demo.entity.Scooter;
import com.example.demo.entity.User;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.PricingMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.BookingService;
import com.example.demo.service.EmailService;
import com.example.demo.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务实现类
 * 实现订单（租赁）相关的具体业务逻辑
 * 包括创建订单、支付、取消、延期、统计等功能
 */
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private PricingMapper pricingMapper;

    @Autowired
    private ScooterMapper scooterMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScooterService scooterService;

    @Autowired
    private EmailService emailService;

    @Override
    public List<Booking> findAll() {
        return bookingMapper.findAll();
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookingMapper.findByUserId(userId);
    }

    @Override
    public Booking findById(Long id) {
        return bookingMapper.findById(id);
    }

    /**
     * 创建新订单
     * 1. 根据hireOption获取价格
     * 2. 设置订单状态为PENDING
     * 3. 生成确认码
     */
    @Override
    public boolean save(Booking booking) {
        Pricing pricing = pricingMapper.findById(getPricingIdByOption(booking.getHireOption()));
        if (pricing != null) {
            booking.setTotalCost(pricing.getPrice());
        }
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return bookingMapper.insert(booking) > 0;
    }

    @Override
    public boolean update(Booking booking) {
        return bookingMapper.update(booking) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return bookingMapper.deleteById(id) > 0;
    }

    /**
     * 延长租期
     * 1. 查找订单，检查状态必须是ACTIVE
     * 2. 计算新的结束时间
     * 3. 增加相应费用
     */
    @Override
    public boolean extendBooking(Long id, String hireOption) {
        Booking booking = bookingMapper.findById(id);
        // 支付完成后状态为 PAID，仍应允许延长（与前端「进行中/已支付」一致）
        if (booking == null || !("ACTIVE".equals(booking.getStatus()) || "PAID".equals(booking.getStatus()))) {
            return false;
        }
        LocalDateTime newEndTime = calculateEndTime(booking.getEndTime(), hireOption);
        booking.setEndTime(newEndTime);

        Pricing pricing = pricingMapper.findById(getPricingIdByOption(hireOption));
        if (pricing != null) {
            booking.setTotalCost(booking.getTotalCost().add(pricing.getPrice()));
        }
        booking.setStatus("ACTIVE");
        return bookingMapper.update(booking) > 0;
    }

    /**
     * 取消订单
     * 1. 检查订单状态（不能是已完成或已取消）
     * 2. 更新状态为CANCELLED
     * 3. 释放车辆（状态改回AVAILABLE）
     */
    @Override
    @Transactional
    public boolean cancelBooking(Long id) {
        Booking booking = bookingMapper.findById(id);
        if (booking == null || "COMPLETED".equals(booking.getStatus()) || "CANCELLED".equals(booking.getStatus())) {
            return false;
        }
        booking.setStatus("CANCELLED");

        if (booking.getScooterId() != null) {
            scooterService.updateStatus(booking.getScooterId(), "AVAILABLE");
        }
        return bookingMapper.update(booking) > 0;
    }

    /**
     * 支付订单
     * 1. 检查订单状态必须是PENDING
     * 2. 更新状态为PAID，设置开始和结束时间
     * 3. 更新车辆状态为IN_USE（使用中）
     * 4. 发送确认邮件
     */
    @Override
    @Transactional
    public boolean payBooking(Long id) {
        Booking booking = bookingMapper.findById(id);
        if (booking == null || !"PENDING".equals(booking.getStatus())) {
            return false;
        }
        booking.setStatus("PAID");
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(calculateEndTime(booking.getStartTime(), booking.getHireOption()));

        if (booking.getScooterId() != null) {
            scooterService.updateStatus(booking.getScooterId(), "IN_USE");
        }

        sendConfirmationEmail(booking);
        return bookingMapper.update(booking) > 0;
    }

    /**
     * 发送预订确认邮件
     * 包含：确认码、车辆编号、租赁选项、时间、总费用
     */
    private void sendConfirmationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null || user.getEmail() == null) {
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : "N/A";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().toString() : "N/A";
            String endTime = booking.getEndTime() != null ? booking.getEndTime().toString() : "N/A";

            emailService.sendBookingConfirmation(
                user.getEmail(),
                user.getUsername(),
                booking.getConfirmationCode(),
                scooterNumber,
                booking.getHireOption(),
                startTime,
                endTime,
                booking.getTotalCost() != null ? booking.getTotalCost().doubleValue() : 0.0
            );
        } catch (Exception e) {
            System.err.println("发送确认邮件失败: " + e.getMessage());
        }
    }

    /**
     * 根据租赁选项获取价格ID
     * 1hr -> 1, 4hr -> 2, 1day -> 3, 1week -> 4
     */
    private Long getPricingIdByOption(String option) {
        return switch (option) {
            case "1hr" -> 1L;
            case "4hr" -> 2L;
            case "1day" -> 3L;
            case "1week" -> 4L;
            default -> 1L;
        };
    }

    /**
     * 计算结束时间
     * 根据租赁选项计算租期结束时间
     */
    private LocalDateTime calculateEndTime(LocalDateTime startTime, String hireOption) {
        return switch (hireOption) {
            case "1hr" -> startTime.plusHours(1);
            case "4hr" -> startTime.plusHours(4);
            case "1day" -> startTime.plusDays(1);
            case "1week" -> startTime.plusWeeks(1);
            default -> startTime.plusHours(1);
        };
    }

    /**
     * 获取用户统计信息
     * 返回：订单总数、总消费金额、总租赁时长
     */
    @Override
    public Map<String, Object> getUserStats(Long userId) {
        int totalBookings = bookingMapper.countByUserId(userId);
        double totalCost = bookingMapper.sumTotalCostByUserId(userId);

        List<Booking> userBookings = bookingMapper.findByUserId(userId);
        double totalDuration = 0;
        for (Booking b : userBookings) {
            if ("PAID".equals(b.getStatus()) || "COMPLETED".equals(b.getStatus())) {
                totalDuration += switch (b.getHireOption()) {
                    case "1hr" -> 1;
                    case "4hr" -> 4;
                    case "1day" -> 24;
                    case "1week" -> 168;
                    default -> 1;
                };
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", totalBookings);
        stats.put("totalDuration", totalDuration);
        stats.put("totalCost", totalCost);
        return stats;
    }
}
