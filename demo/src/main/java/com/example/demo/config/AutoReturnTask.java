package com.example.demo.config;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Depot;
import com.example.demo.entity.Scooter;
import com.example.demo.entity.User;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.DepotMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.EmailService;
import com.example.demo.service.ScooterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 自动还车定时任务
 * 功能：
 * 1. 每 1 分钟检查一次超时订单
 * 2. 对超时订单自动还车到最近的服务点
 * 3. 发送超时提醒邮件
 */
@Component
public class AutoReturnTask {

    private static final Logger logger = LoggerFactory.getLogger(AutoReturnTask.class);

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private DepotMapper depotMapper;

    @Autowired
    private ScooterMapper scooterMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScooterService scooterService;

    @Autowired
    private EmailService emailService;

    /**
     * 每 1 分钟执行一次自动还车检查
     * cron: "0 * * * * ?" = 每分钟的第 0 秒执行
     */
    @Scheduled(fixedRate = 60000) // 每 60 秒执行一次
    @Transactional
    public void autoReturnOverdueBookings() {
        logger.info("=== 开始检查超时订单 ===");

        // 查找所有进行中的订单
        List<Booking> activeBookings = bookingMapper.findAll();
        int autoReturnedCount = 0;

        for (Booking booking : activeBookings) {
            // 只处理状态为 PAID 或 ACTIVE 的订单
            if (!"PAID".equals(booking.getStatus()) && !"ACTIVE".equals(booking.getStatus())) {
                continue;
            }

            // 检查是否超时
            if (booking.getEndTime() != null && LocalDateTime.now().isAfter(booking.getEndTime())) {
                logger.info("发现超时订单: id={}, 用户={}, 结束时间={}",
                    booking.getId(), booking.getUserId(), booking.getEndTime());

                // 自动还车
                boolean success = autoReturnBooking(booking);
                if (success) {
                    autoReturnedCount++;
                    logger.info("自动还车成功: 订单id={}", booking.getId());
                }
            }
        }

        if (autoReturnedCount > 0) {
            logger.info("=== 本次检查完成，自动还车 {} 个订单 ===", autoReturnedCount);
        }
    }

    /**
     * 自动还车到最近的服务点
     */
    private boolean autoReturnBooking(Booking booking) {
        try {
            // 获取车辆当前位置
            Long scooterId = booking.getScooterId();
            Scooter scooter = null;
            if (scooterId != null) {
                scooter = scooterMapper.findById(scooterId);
            }

            // 查找最近的服务点（如果有车辆位置则按距离，否则用取车时的服务点）
            Depot nearestDepot = findNearestOrDefaultDepot(scooter, booking.getStartDepotId());

            if (nearestDepot == null) {
                logger.error("无法找到可用的服务点进行自动还车，订单id={}", booking.getId());
                return false;
            }

            // 更新订单状态
            booking.setStatus("COMPLETED");
            booking.setEndTime(LocalDateTime.now());
            booking.setEndDepotId(nearestDepot.getId());

            // 更新车辆状态和位置
            if (scooterId != null) {
                scooterService.updateStatusAndDepot(scooterId, "AVAILABLE", nearestDepot.getId());
            }

            // 保存订单
            boolean updated = bookingMapper.update(booking) > 0;

            // 发送超时通知邮件
            if (updated) {
                sendAutoReturnEmail(booking, nearestDepot.getName());
            }

            return updated;
        } catch (Exception e) {
            logger.error("自动还车失败: 订单id={}, 错误={}", booking.getId(), e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查找最近的服务点或使用默认服务点
     * 优先使用取车时的服务点，其次选择第一个可用服务点
     */
    private Depot findNearestOrDefaultDepot(Scooter scooter, Long startDepotId) {
        // 1. 如果有取车时的服务点，优先使用
        if (startDepotId != null) {
            Depot startDepot = depotMapper.findById(startDepotId);
            if (startDepot != null && "ACTIVE".equals(startDepot.getStatus())) {
                return startDepot;
            }
        }

        // 2. 如果有车辆位置，计算最近的服务点
        if (scooter != null && scooter.getLatitude() != null && scooter.getLongitude() != null) {
            List<Depot> activeDepots = depotMapper.findAllActive();
            Depot nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (Depot depot : activeDepots) {
                if (depot.getLatitude() != null && depot.getLongitude() != null) {
                    double distance = calculateDistance(
                        scooter.getLatitude(), scooter.getLongitude(),
                        depot.getLatitude().doubleValue(), depot.getLongitude().doubleValue()
                    );
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = depot;
                    }
                }
            }

            if (nearest != null) {
                return nearest;
            }
        }

        // 3. 返回任意一个可用服务点
        List<Depot> activeDepots = depotMapper.findAllActive();
        return activeDepots.isEmpty() ? null : activeDepots.get(0);
    }

    /**
     * 计算两点之间的直线距离（简化版，单位：度，实际应用中应使用更精确的算法）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDiff = lat2 - lat1;
        double lonDiff = lon2 - lon1;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    /**
     * 发送自动还车通知邮件
     */
    private void sendAutoReturnEmail(Booking booking, String depotName) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : "N/A";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().toString() : "N/A";
            String endTime = booking.getEndTime() != null ? booking.getEndTime().toString() : LocalDateTime.now().toString();

            emailService.sendAutoReturnNotification(
                user.getEmail(),
                user.getUsername(),
                booking.getConfirmationCode(),
                scooterNumber,
                startTime,
                endTime,
                depotName,
                booking.getTotalCost() != null ? booking.getTotalCost().doubleValue() : 0.0
            );
        } catch (Exception e) {
            logger.error("发送自动还车通知邮件失败: {}", e.getMessage());
        }
    }
}
