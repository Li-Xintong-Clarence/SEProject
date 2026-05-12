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
import com.example.demo.service.DiscountService;
import com.example.demo.service.EmailService;
import com.example.demo.service.ScooterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务实现??
 * 实现订单（租赁）相关的具体业务逻辑
 * 包括创建订单、支付、取消、延期、统计等功能
 */
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

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

    @Autowired
    private DiscountService discountService;

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
     * 通过服务点创建订单（自动分配车辆??
     * 1. 检查用户是否有进行中的订单
     * 2. 检查服务点是否有可用车??
     * 3. 分配第一辆可用车??
     * 4. 创建订单
     */
    @Override
    @Transactional
    public Booking createByDepot(Long userId, Long depotId, String hireOption) {
        // 检查用户是否有进行中的订单
        List<Booking> activeBookings = bookingMapper.findByUserId(userId);
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return null;
            }
        }

        // 查找该服务点的可用车??
        Scooter scooter = scooterService.findFirstAvailableByDepotId(depotId);
        if (scooter == null) {
            return null; // 没有可用车辆
        }

        // 创建订单
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setScooterId(scooter.getId());
        booking.setStartDepotId(depotId);
        booking.setHireOption(hireOption);
        booking.setStatus(""PENDING"");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // 计算价格
        Pricing pricing = pricingMapper.findById(getPricingIdByOption(hireOption));
        if (pricing != null) {
            User bookingUser = userMapper.findById(userId);
            BigDecimal originalPrice = pricing.getPrice();
            double finalPrice = discountService.calculateDiscountedPrice(originalPrice.doubleValue(),
                    bookingUser != null ? bookingUser.getUserType() : ""NORMAL"");
            booking.setTotalCost(BigDecimal.valueOf(finalPrice));
        }

        if (bookingMapper.insert(booking) > 0) {
            return booking;
        }
        return null;
    }

    /**
     * 创建新订单（指定车辆??
     * 1. 检查用户是否有进行中的订单（一人一车限制）
     * 2. 根据hireOption获取价格
     * 3. 设置订单状态为PENDING
     * 4. 生成确认??
     */
    @Override
    public boolean save(Booking booking) {
        logger.info(""=== BookingService.save() 开??===""");
        logger.info(""用户ID: {}, 滑板车ID: {}, 租赁选项: {}"",
                    booking.getUserId(), booking.getScooterId(), booking.getHireOption());

        // 一人一车限制：检查用户是否有进行中的订单
        List<Booking> activeBookings = bookingMapper.findByUserId(booking.getUserId());
        logger.info(""该用户已有订单数: {}"", activeBookings.size());
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                logger.warn(""用户已有进行中的订单，订单ID: {}, 状?? {}"", b.getId(), b.getStatus());
                return false; // 用户已有进行中的订单
            }
        }

        Pricing pricing = pricingMapper.findById(getPricingIdByOption(booking.getHireOption()));
        logger.info(""获取到的价格信息: {}"", pricing);

        if (pricing != null) {
            // 获取用户信息用于折扣计算
            User bookingUser = userMapper.findById(booking.getUserId());
            BigDecimal originalPrice = pricing.getPrice();
            // 计算折后价格
            double finalPrice = discountService.calculateDiscountedPrice(originalPrice.doubleValue(),
                    bookingUser != null ? bookingUser.getUserType() : ""NORMAL"");
            booking.setTotalCost(BigDecimal.valueOf(finalPrice));
            logger.info(""计算后价?? {} -> {}"", originalPrice, finalPrice);
        }
        booking.setStatus(""PENDING"");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        logger.info(""准备插入订单: userId={}, scooterId={}, status={}, totalCost={}"",
                    booking.getUserId(), booking.getScooterId(), booking.getStatus(), booking.getTotalCost());

        int result = bookingMapper.insert(booking);
        logger.info(""插入结果: {}, 新订单ID: {}"", result, booking.getId());

        if (result > 0) {
            logger.info(""=== BookingService.save() 成功 ==="");
        } else {
            logger.error(""=== BookingService.save() 失败，insert返回0 ==="");
        }
        return result > 0;
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
        // 允许 PAID ??ACTIVE 状态的订单延长
        if (booking == null || (!""ACTIVE"".equals(booking.getStatus()) && !""PAID"".equals(booking.getStatus()))) {
            return false;
        }

        LocalDateTime newEndTime = calculateEndTime(booking.getEndTime(), hireOption);
        booking.setEndTime(newEndTime);

        Pricing pricing = pricingMapper.findById(getPricingIdByOption(hireOption));
        if (pricing != null) {
            booking.setTotalCost(booking.getTotalCost().add(pricing.getPrice()));
        }

        return bookingMapper.update(booking) > 0;
    }

    /**
     * 取消订单
     * 1. 检查订单状态（不能是已完成或已取消??
     * 2. 更新状态为CANCELLED
     * 3. 释放车辆（状态改回AVAILABLE??
     * 4. 发送取消邮??
     */
    @Override
    @Transactional
    public boolean cancelBooking(Long id) {
        Booking booking = bookingMapper.findById(id);
        if (booking == null || ""COMPLETED"".equals(booking.getStatus()) || ""CANCELLED"".equals(booking.getStatus())) {
            return false;
        }
        booking.setStatus(""CANCELLED"");

        if (booking.getScooterId() != null) {
            scooterService.updateStatus(booking.getScooterId(), ""AVAILABLE"");
        }

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            sendCancellationEmail(booking);
        }
        return updated;
    }

    /**
     * 还车（结束骑行）
     * 1. 检查订单状态必须是PAID或ACTIVE
     * 2. 更新状态为COMPLETED
     * 3. 释放车辆（状态改回AVAILABLE??
     * 4. 发送结束邮??
     */
    @Override
    @Transactional
    public boolean returnScooter(Long id) {
        Booking booking = bookingMapper.findById(id);
        if (booking == null || !(""PAID"".equals(booking.getStatus()) || ""ACTIVE"".equals(booking.getStatus()))) {
            return false;
        }
        booking.setStatus(""COMPLETED"");
        booking.setEndTime(LocalDateTime.now());

        if (booking.getScooterId() != null) {
            scooterService.updateStatus(booking.getScooterId(), ""AVAILABLE"");
        }

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            sendCompletionEmail(booking);
        }
        return updated;
    }

    /**
     * 支付订单
     * 1. 检查订单状态必须是PENDING
     * 2. 更新状态为PAID，设置开始和结束时间
     * 3. 更新车辆状态为IN_USE（使用中??
     * 4. 发送确认邮??
     */
    @Override
    @Transactional
    public boolean payBooking(Long id) {
        Booking booking = bookingMapper.findById(id);
        if (booking == null || !""PENDING"".equals(booking.getStatus())) {
            return false;
        }
        booking.setStatus(""PAID"");
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(calculateEndTime(booking.getStartTime(), booking.getHireOption()));

        if (booking.getScooterId() != null) {
            scooterService.updateStatus(booking.getScooterId(), ""IN_USE"");
        }

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            sendConfirmationEmail(booking);
        }
        return updated;
    }

    /**
     * 发送预订确认邮??
     * 包含：确认码、车辆编号、租赁选项、时间、总费??
     */
    private void sendConfirmationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println(""发送确认邮件失败：找不到用户，userId="" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println(""发送确认邮件失败：用户邮箱为空，userId="" + booking.getUserId());
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : ""N/A"";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().toString() : ""N/A"";
            String endTime = booking.getEndTime() != null ? booking.getEndTime().toString() : ""N/A"";

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
            System.err.println(""发送确认邮件异?? "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送取消预订邮??
     */
    private void sendCancellationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println(""发送取消邮件失败：找不到用户，userId="" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println(""发送取消邮件失败：用户邮箱为空，userId="" + booking.getUserId());
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : ""N/A"";

            emailService.sendBookingCancellation(
                user.getEmail(),
                user.getUsername(),
                booking.getConfirmationCode(),
                scooterNumber,
                booking.getHireOption()
            );
        } catch (Exception e) {
            System.err.println(""发送取消邮件异?? "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送结束骑行邮??
     */
    private void sendCompletionEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println(""发送结束邮件失败：找不到用户，userId="" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println(""发送结束邮件失败：用户邮箱为空，userId="" + booking.getUserId());
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : ""N/A"";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().toString() : ""N/A"";
            String endTime = booking.getEndTime() != null ? booking.getEndTime().toString() : ""N/A"";

            emailService.sendRideCompletion(
                user.getEmail(),
                user.getUsername(),
                booking.getConfirmationCode(),
                scooterNumber,
                startTime,
                endTime,
                booking.getTotalCost() != null ? booking.getTotalCost().doubleValue() : 0.0
            );
        } catch (Exception e) {
            System.err.println(""发送结束邮件异?? "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据租赁选项获取价格ID
     * 1hr -> 1, 4hr -> 2, 1day -> 3, 1week -> 4
     */
    private Long getPricingIdByOption(String option) {
        return switch (option) {
            case ""1hr"" -> 1L;
            case ""4hr"" -> 2L;
            case ""1day"" -> 3L;
            case ""1week"" -> 4L;
            default -> 1L;
        };
    }

    /**
     * 计算结束时间
     * 根据租赁选项计算租期结束时间
     */
    private LocalDateTime calculateEndTime(LocalDateTime startTime, String hireOption) {
        return switch (hireOption) {
            case ""1hr"" -> startTime.plusHours(1);
            case ""4hr"" -> startTime.plusHours(4);
            case ""1day"" -> startTime.plusDays(1);
            case ""1week"" -> startTime.plusWeeks(1);
            default -> startTime.plusHours(1);
        };
    }

    /**
     * 获取用户统计信息
     * 返回：订单总数、总消费金额、总租赁时??
     */
    @Override
    public Map<String, Object> getUserStats(Long userId) {
        int totalBookings = bookingMapper.countByUserId(userId);
        double totalCost = bookingMapper.sumTotalCostByUserId(userId);

        List<Booking> userBookings = bookingMapper.findByUserId(userId);
        double totalDuration = 0;
        for (Booking b : userBookings) {
            if (""PAID"".equals(b.getStatus()) || ""COMPLETED"".equals(b.getStatus())) {
                totalDuration += switch (b.getHireOption()) {
                    case ""1hr"" -> 1;
                    case ""4hr"" -> 4;
                    case ""1day"" -> 24;
                    case ""1week"" -> 168;
                    default -> 1;
                };
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put(""totalBookings"", totalBookings);
        stats.put(""totalDuration"", totalDuration);
        stats.put(""totalCost"", totalCost);
        return stats;
    }
}
