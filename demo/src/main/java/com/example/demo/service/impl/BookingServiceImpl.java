package com.example.demo.service.impl;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Depot;
import com.example.demo.entity.Pricing;
import com.example.demo.entity.Scooter;
import com.example.demo.entity.User;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.DepotMapper;
import com.example.demo.mapper.PricingMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.BookingService;
import com.example.demo.service.DiscountService;
import com.example.demo.service.EmailService;
import com.example.demo.service.PaymentService;
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
 * Booking Service Implementation
 * Implements booking-related business logic
 * Supports booking creation, cancellation, statistics, etc.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private DepotMapper depotMapper;

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

    @Autowired
    private PaymentService paymentService;

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
     * Create booking by depot (auto-assign scooter)
     * 1. Check if user has active bookings
     * 2. Check if depot has available scooters
     * 3. Find first available scooter
     * 4. Create booking
     */
    @Override
    @Transactional
    public Booking createByDepot(Long userId, Long depotId, String hireOption) {
        // Check if user has active bookings
        List<Booking> activeBookings = bookingMapper.findByUserId(userId);
        for (Booking b : activeBookings) {
            if ("PAID".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus())) {
                return null;
            }
        }

        // Find available scooter in depot
        Scooter scooter = scooterService.findFirstAvailableByDepotId(depotId);
        if (scooter == null) {
            return null; // No available scooters
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setScooterId(scooter.getId());
        booking.setStartDepotId(depotId);
        booking.setHireOption(hireOption);
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Calculate pricing
        Pricing pricing = pricingMapper.findById(getPricingIdByOption(hireOption));
        if (pricing != null) {
            User bookingUser = userMapper.findById(userId);
            BigDecimal originalPrice = pricing.getPrice();
            double finalPrice = discountService.calculateDiscountedPriceForUser(originalPrice.doubleValue(), bookingUser);
            booking.setTotalCost(BigDecimal.valueOf(finalPrice));
        }

        if (bookingMapper.insert(booking) > 0) {
            return booking;
        }
        return null;
    }

    /**
     * Save new booking with specific scooter
     * 1. One-to-one principle: check if user has active bookings
     * 2. Get pricing based on hireOption
     * 3. Set booking status to PENDING
     * 4. Generate confirmation code
     */
    @Override
    public boolean save(Booking booking) {
        logger.info("=== BookingService.save() called ===");
        logger.info("UserID: {}, ScooterID: {}, HireOption: {}",
                    booking.getUserId(), booking.getScooterId(), booking.getHireOption());

        // One-to-one principle: check if user has active bookings
        List<Booking> activeBookings = bookingMapper.findByUserId(booking.getUserId());
        logger.info("User has {} bookings", activeBookings.size());
        for (Booking b : activeBookings) {
            if ("PAID".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus())) {
                logger.warn("User has active booking, ID: {}, Status: {}", b.getId(), b.getStatus());
                return false; // User already has active booking
            }
        }

        Pricing pricing = pricingMapper.findById(getPricingIdByOption(booking.getHireOption()));
        logger.info("Retrieved pricing info: {}", pricing);

        if (pricing != null) {
            // Get user info and calculate discount
            User bookingUser = userMapper.findById(booking.getUserId());
            BigDecimal originalPrice = pricing.getPrice();
            double finalPrice = discountService.calculateDiscountedPriceForUser(originalPrice.doubleValue(), bookingUser);
            booking.setTotalCost(BigDecimal.valueOf(finalPrice));
            logger.info("Price {} -> {}", originalPrice, finalPrice);
        }
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        logger.info("Ready to insert booking: userId={}, scooterId={}, status={}, totalCost={}",
                    booking.getUserId(), booking.getScooterId(), booking.getStatus(), booking.getTotalCost());

        int result = bookingMapper.insert(booking);
        logger.info("Insert result: {}, new booking ID: {}", result, booking.getId());

        if (result > 0) {
            logger.info("=== BookingService.save() success ===");
        } else {
            logger.error("=== BookingService.save() failed: insert returns 0 ===");
        }
        return result > 0;
    }

    /**
     * Admin: Create booking for user without checking active bookings
     * This allows admin to create bookings even if user already has an active ride
     */
    @Override
    public boolean adminSave(Booking booking) {
        logger.info("=== BookingService.adminSave() called (admin override) ===");
        logger.info("UserID: {}, ScooterID: {}, HireOption: {}, BookingType: {}",
                    booking.getUserId(), booking.getScooterId(), booking.getHireOption(), booking.getBookingType());

        // Skip active booking check - admin has authority to override
        // But still calculate pricing
        Pricing pricing = pricingMapper.findById(getPricingIdByOption(booking.getHireOption()));
        logger.info("Retrieved pricing info: {}", pricing);

        // ID7: Guest 预订不享受折扣
        boolean isGuest = "GUEST".equals(booking.getBookingType()) || booking.getUserId() == null;
        if (pricing != null) {
            if (isGuest) {
                // Guest 预订使用原价
                booking.setTotalCost(pricing.getPrice());
            } else {
                User bookingUser = userMapper.findById(booking.getUserId());
                BigDecimal originalPrice = pricing.getPrice();
                double finalPrice = discountService.calculateDiscountedPriceForUser(originalPrice.doubleValue(), bookingUser);
                booking.setTotalCost(BigDecimal.valueOf(finalPrice));
            }
            logger.info("Price set: {}", booking.getTotalCost());
        } else {
            // 如果没有找到定价，设置默认值
            logger.warn("Pricing not found for option: {}, using default price", booking.getHireOption());
            booking.setTotalCost(BigDecimal.valueOf(10.0)); // 默认价格
        }

        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        logger.info("Ready to insert booking: userId={}, scooterId={}, status={}, totalCost={}, bookingType={}, guestName={}",
                    booking.getUserId(), booking.getScooterId(), booking.getStatus(), booking.getTotalCost(),
                    booking.getBookingType(), booking.getGuestName());

        int result = bookingMapper.insert(booking);
        logger.info("Insert result: {}, new booking ID: {}", result, booking.getId());

        if (result > 0) {
            logger.info("=== BookingService.adminSave() success ===");
        } else {
            logger.error("=== BookingService.adminSave() failed: insert returns 0 ===");
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
     * Extend booking
     * 1. Find booking with status PAID or ACTIVE
     * 2. Calculate new end time
     * 3. Add corresponding cost
     */
    @Override
    public boolean extendBooking(Long id, String hireOption) {
        Booking booking = bookingMapper.findById(id);
        // Only PAID or ACTIVE status bookings can be extended
        if (booking == null || (!"ACTIVE".equals(booking.getStatus()) && !"PAID".equals(booking.getStatus()))) {
            return false;
        }

        LocalDateTime newEndTime = calculateEndTime(booking.getEndTime(), hireOption);
        booking.setEndTime(newEndTime);

        Pricing pricing = pricingMapper.findById(getPricingIdByOption(hireOption));
        if (pricing != null) {
            BigDecimal currentCost = booking.getTotalCost() != null ? booking.getTotalCost() : BigDecimal.ZERO;
            booking.setTotalCost(currentCost.add(pricing.getPrice()));
        }

        return bookingMapper.update(booking) > 0;
    }

    /**
     * Cancel booking
     * 1. Only pending/paid/active bookings can be cancelled
     * 2. Set status to CANCELLED
     * 3. Release scooter status to AVAILABLE
     * 4. Send cancellation email
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

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            sendCancellationEmail(booking);
        }
        return updated;
    }

    /**
     * Return scooter (complete booking)
     * 1. Only paid/active bookings can be returned
     * 2. Set status to COMPLETED
     * 3. Update scooter: status to AVAILABLE, battery -30%, location to end depot
     * 4. Update depot counts (start depot -1, end depot +1 if different)
     * 5. Send completion email
     * @param id Booking ID
     * @param endDepotId Depot ID where scooter is returned (optional, uses start depot if null)
     */
    @Override
    @Transactional
    public boolean returnScooter(Long id, Long endDepotId) {
        Booking booking = bookingMapper.findById(id);
        if (booking == null || !("PAID".equals(booking.getStatus()) || "ACTIVE".equals(booking.getStatus()))) {
            return false;
        }

        // Determine end depot
        Long actualEndDepotId = endDepotId != null ? endDepotId : booking.getStartDepotId();
        booking.setEndDepotId(actualEndDepotId);
        booking.setStatus("COMPLETED");
        booking.setEndTime(LocalDateTime.now());

        // Update scooter: status, battery, location
        if (booking.getScooterId() != null) {
            Scooter scooter = scooterMapper.findById(booking.getScooterId());
            if (scooter != null) {
                // Update battery level (reduce by 30%, minimum 0)
                BigDecimal currentBattery = scooter.getBatteryLevel();
                if (currentBattery != null) {
                    double newBattery = Math.max(0, currentBattery.doubleValue() - 30);
                    scooter.setBatteryLevel(BigDecimal.valueOf(newBattery));
                }

                // Update location to end depot
                if (actualEndDepotId != null) {
                    Depot endDepot = depotMapper.findById(actualEndDepotId);
                    if (endDepot != null) {
                        scooter.setDepotId(actualEndDepotId);
                        scooter.setLocation(endDepot.getName());
                        // Update coordinates if depot has them
                        if (endDepot.getLatitude() != null) {
                            scooter.setLatitude(endDepot.getLatitude().doubleValue());
                        }
                        if (endDepot.getLongitude() != null) {
                            scooter.setLongitude(endDepot.getLongitude().doubleValue());
                        }
                    }
                }

                // Update scooter status to AVAILABLE
                scooterService.updateStatus(booking.getScooterId(), "AVAILABLE");
                scooterMapper.update(scooter);
            }
        }

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            sendCompletionEmail(booking);
        }
        return updated;
    }

    /**
     * Return scooter (complete booking) - original method for backward compatibility
     * Uses start depot as end depot
     */
    @Override
    @Transactional
    public boolean returnScooter(Long id) {
        return returnScooter(id, null);
    }

    /**
     * Pay booking
     * 1. Only pending status bookings can be paid
     * 2. Set status to PAID and set start/end times
     * 3. Set scooter status to IN_USE
     * 4. Send confirmation email
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

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            sendConfirmationEmail(booking);
        }
        return updated;
    }

    /**
     * Pay booking with enhanced security
     * 1. Validate payment password if set
     * 2. Process payment with tokenization
     * 3. Only pending status bookings can be paid
     * 4. Set status to PAID and set start/end times
     * 5. Set scooter status to IN_USE
     * 6. Send confirmation email
     */
    @Override
    @Transactional
    public boolean payBooking(Long id, Long userId, String cardLast4, double amount, String paymentMethod, String paymentPassword) {
        logger.info("=== Enhanced payBooking called ===");
        logger.info("bookingId={}, userId={}, amount={}, method={}", id, userId, amount, paymentMethod);

        // 获取用户信息验证支付密码
        User user = userMapper.findById(userId);
        if (user != null && user.getPaymentPassword() != null && !user.getPaymentPassword().isEmpty()) {
            // 用户设置了支付密码，必须验证
            if (paymentPassword == null || paymentPassword.isEmpty()) {
                logger.warn("支付失败：需要支付密码");
                return false;
            }
            if (!paymentService.verifyPaymentPassword(userId, paymentPassword)) {
                logger.warn("支付失败：支付密码错误");
                return false;
            }
        }

        // 记录支付日志（Token化后的卡号）
        if (cardLast4 != null) {
            logger.info("支付信息: userId={}, cardLast4=****{}, amount={}, method={}",
                    userId, cardLast4, amount, paymentMethod);
        }

        // 处理支付
        boolean paymentSuccess = paymentService.processPayment(userId, cardLast4, amount, paymentMethod);
        if (!paymentSuccess) {
            logger.warn("支付处理失败");
            return false;
        }

        // 更新预订状态
        Booking booking = bookingMapper.findById(id);
        if (booking == null || !"PENDING".equals(booking.getStatus())) {
            logger.warn("预订不存在或状态不是PENDING, bookingId={}", id);
            return false;
        }

        booking.setStatus("PAID");
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(calculateEndTime(booking.getStartTime(), booking.getHireOption()));

        if (booking.getScooterId() != null) {
            scooterService.updateStatus(booking.getScooterId(), "IN_USE");
        }

        boolean updated = bookingMapper.update(booking) > 0;
        if (updated) {
            logger.info("预订状态更新成功，发送确认邮件");
            sendConfirmationEmail(booking);
        }

        return updated;
    }

    /**
     * Send booking confirmation email
     * Includes confirmation code, scooter number, hire option, time, total fee
     */
    private void sendConfirmationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println("Booking confirmation email failed: user not found, userId=" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("Booking confirmation email failed: user email is empty, userId=" + booking.getUserId());
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
            System.err.println("Booking confirmation email error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send cancellation confirmation email
     */
    private void sendCancellationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println("Booking cancellation email failed: user not found, userId=" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("Booking cancellation email failed: user email is empty, userId=" + booking.getUserId());
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : "N/A";

            emailService.sendBookingCancellation(
                user.getEmail(),
                user.getUsername(),
                booking.getConfirmationCode(),
                scooterNumber,
                booking.getHireOption()
            );
        } catch (Exception e) {
            System.err.println("Booking cancellation email error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send ride completion email
     */
    private void sendCompletionEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println("Ride completion email failed: user not found, userId=" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("Ride completion email failed: user email is empty, userId=" + booking.getUserId());
                return;
            }

            Scooter scooter = null;
            if (booking.getScooterId() != null) {
                scooter = scooterMapper.findById(booking.getScooterId());
            }

            String scooterNumber = scooter != null ? scooter.getScooterNumber() : "N/A";
            String startTime = booking.getStartTime() != null ? booking.getStartTime().toString() : "N/A";
            String endTime = booking.getEndTime() != null ? booking.getEndTime().toString() : "N/A";

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
            System.err.println("Ride completion email error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get pricing ID by hire option
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
     * Calculate end time
     * Based on hire option and start time
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
     * Get user statistics
     * Returns: total bookings, total duration, total cost, weekly usage for frequent user detection
     */
    @Override
    public Map<String, Object> getUserStats(Long userId) {
        int totalBookings = bookingMapper.countByUserId(userId);
        double totalCost = bookingMapper.sumTotalCostByUserId(userId);
        Integer weeklyHours = bookingMapper.getUserWeeklyHours(userId);
        if (weeklyHours == null) weeklyHours = 0;

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
        stats.put("weeklyHours", weeklyHours);
        stats.put("isFrequentUser", weeklyHours >= 8);
        return stats;
    }
}
