package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Scooter;
import com.example.demo.service.BookingService;
import com.example.demo.service.ScooterService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Booking Controller
 * Handles booking-related operations: creation, queries, payment, cancellation, extension, etc.
 * Path: /api/bookings/*
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ScooterService scooterService;

    /**
     * Create booking by depot (auto-assign scooter)
     * POST /api/bookings/depot
     * Params: depotId, hireOption
     */
    @PostMapping("/depot")
    public Result<Booking> createByDepot(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        // One-to-one principle: check if user has active bookings
        List<Booking> activeBookings = bookingService.findByUserId(userId);
        for (Booking b : activeBookings) {
            if ("PAID".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus())) {
                return Result.error("You have an active ride. Please complete or cancel it before booking again.");
            }
        }

        Long depotId = Long.parseLong(params.get("depotId"));
        String hireOption = params.get("hireOption");

        Booking booking = bookingService.createByDepot(userId, depotId, hireOption);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error("No available scooters at this depot");
    }

    /**
     * Create booking with specific scooter ID
     * POST /api/bookings
     * Params: scooterId, hireOption, startTime, etc.
     */
    @PostMapping
    public Result<Booking> create(@RequestBody Booking booking, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        booking.setUserId(userId);

        // One-to-one principle: check if user has active bookings
        List<Booking> activeBookings = bookingService.findByUserId(userId);
        for (Booking b : activeBookings) {
            if ("PAID".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus())) {
                return Result.error("You have an active ride. ID:" + b.getId() + ". Please complete or cancel it before booking again.");
            }
        }

        // Validation
        if (booking.getScooterId() == null) {
            return Result.error("Scooter ID cannot be empty");
        }
        if (booking.getHireOption() == null || booking.getHireOption().isEmpty()) {
            return Result.error("Please select rental duration");
        }

        // If startDepotId is not specified, auto-fill from scooter
        if (booking.getStartDepotId() == null) {
            Scooter scooter = scooterService.findById(booking.getScooterId());
            if (scooter != null && scooter.getDepotId() != null) {
                booking.setStartDepotId(scooter.getDepotId());
            }
        }

        boolean saved = bookingService.save(booking);
        if (saved) {
            return Result.success(booking);
        }
        return Result.error("Booking creation failed, please try again later");
    }

    /**
     * Get current user's bookings
     * GET /api/bookings
     */
    @GetMapping
    public Result<List<Booking>> findMyBookings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(bookingService.findByUserId(userId));
    }

    /**
     * Get current active ride
     * GET /api/bookings/current
     * Used to check if user has active ride (one-to-one)
     * Returns pending, paid, or active bookings
     */
    @GetMapping("/current")
    public Result<Booking> getCurrentRide(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Booking> bookings = bookingService.findByUserId(userId);
        for (Booking b : bookings) {
            if ("PAID".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus())) {
                return Result.success(b);
            }
        }
        return Result.error("No active ride");
    }

    /**
     * Get current user's unfinished bookings
     * GET /api/bookings/my/active
     * Returns bookings with status: PENDING, PAID, ACTIVE
     */
    @GetMapping("/my/active")
    public Result<Booking> getMyActiveBooking(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Booking> bookings = bookingService.findByUserId(userId);
        // Return first unfinished booking
        for (Booking b : bookings) {
            String status = b.getStatus();
            // PENDING, PAID, ACTIVE are unfinished statuses
            if (!"COMPLETED".equals(status) && !"CANCELLED".equals(status)) {
                return Result.success(b);
            }
        }
        return Result.error("No active booking");
    }

    /**
     * Get booking by ID
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    public Result<Booking> findById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error("Booking not found");
    }

    /**
     * Extend booking
     * PUT /api/bookings/{id}/extend?hireOption=1day
     * Params: hireOption - extension duration option
     */
    @PutMapping("/{id}/extend")
    public Result<Booking> extend(@PathVariable Long id, @RequestParam String hireOption) {
        if (bookingService.extendBooking(id, hireOption)) {
            // Return updated booking info
            Booking updatedBooking = bookingService.findById(id);
            return Result.success(updatedBooking);
        }
        return Result.error("Failed to extend booking");
    }

    /**
     * Cancel booking
     * POST /api/bookings/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        if (bookingService.cancelBooking(id)) {
            return Result.success("Booking cancelled successfully");
        }
        return Result.error("Failed to cancel booking");
    }

    /**
     * Return scooter (complete ride)
     * POST /api/bookings/{id}/return
     */
    @PostMapping("/{id}/return")
    public Result<String> returnScooter(@PathVariable Long id) {
        if (bookingService.returnScooter(id)) {
            return Result.success("Scooter returned successfully");
        }
        return Result.error("Failed to return scooter");
    }

    /**
     * Pay booking
     * POST /api/bookings/{id}/pay
     * Params: cardLast4, amount, paymentMethod (optional), paymentPassword (optional for security)
     */
    @PostMapping("/{id}/pay")
    public Result<String> pay(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> paymentData, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        double amount = 0;
        String cardLast4 = null;
        String paymentMethod = "credit";
        String paymentPassword = null;

        if (paymentData != null) {
            if (paymentData.get("amount") != null) {
                amount = Double.parseDouble(paymentData.get("amount").toString());
            }
            if (paymentData.get("cardLast4") != null) {
                cardLast4 = paymentData.get("cardLast4").toString();
            }
            if (paymentData.get("paymentMethod") != null) {
                paymentMethod = paymentData.get("paymentMethod").toString();
            }
            if (paymentData.get("paymentPassword") != null) {
                paymentPassword = paymentData.get("paymentPassword").toString();
            }
        }

        // 使用增强的支付服务（包含Tokenization和支付密码验证）
        boolean success = bookingService.payBooking(id, userId, cardLast4, amount, paymentMethod, paymentPassword);
        if (success) {
            logger.info("支付成功, bookingId={}, userId={}, amount={}", id, userId, amount);
            return Result.success("Payment successful");
        }
        return Result.error("Payment failed");
    }

    /**
     * Get booking confirmation info (includes confirmation code)
     * GET /api/bookings/{id}/confirmation
     */
    @GetMapping("/{id}/confirmation")
    public Result<Map<String, Object>> getConfirmation(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            return Result.error("Booking not found");
        }
        Map<String, Object> confirmation = new HashMap<>();
        confirmation.put("confirmationCode", booking.getConfirmationCode());
        confirmation.put("scooterId", booking.getScooterId());
        confirmation.put("hireOption", booking.getHireOption());
        confirmation.put("startTime", booking.getStartTime());
        confirmation.put("endTime", booking.getEndTime());
        confirmation.put("totalCost", booking.getTotalCost());
        confirmation.put("status", booking.getStatus());
        return Result.success(confirmation);
    }
}
