package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Scooter;
import com.example.demo.service.BookingService;
import com.example.demo.service.ScooterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制??
 * 处理租赁订单的创建、查询、支付、取消、延期等操作
 * 路径: /api/bookings/*
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ScooterService scooterService;

    /**
     * 创建新订单（通过服务点租车，自动分配车辆??
     * POST /api/bookings/depot
     * 参数: depotId, hireOption
     */
    @PostMapping(""depot"")
    public Result<Booking> createByDepot(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");

        // 一人一车限制：检查用户是否有进行中的订单
        List<Booking> activeBookings = bookingService.findByUserId(userId);
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return Result.error(""您有正在进行中的行程，请先完成或取消后再创建新订??"");
            }
        }

        Long depotId = Long.parseLong(params.get(""depotId""));
        String hireOption = params.get(""hireOption"");

        Booking booking = bookingService.createByDepot(userId, depotId, hireOption);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error(""该服务点暂无可用车辆"");
    }

    /**
     * 创建新订单（指定车辆ID??
     * POST /api/bookings
     * 参数: scooterId, hireOption, startTime ??
     */
    @PostMapping
    public Result<Booking> create(@RequestBody Booking booking, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        booking.setUserId(userId);

        // 一人一车限制：检查用户是否有进行中的订单
        List<Booking> activeBookings = bookingService.findByUserId(userId);
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return Result.error(""您有正在进行中的行程（订单号:"" + b.getId() + ""），请先完成或取消后再创建新订单"");
            }
        }

        // 检查必要参??
        if (booking.getScooterId() == null) {
            return Result.error(""滑板车ID不能为空"");
        }
        if (booking.getHireOption() == null || booking.getHireOption().isEmpty()) {
            return Result.error(""请选择租赁时长"");
        }

        // 如果没有指定 startDepotId，自动从滑板车获??
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
        return Result.error(""创建订单失败，请稍后重试"");
    }

    /**
     * 获取当前用户的订单列??
     * GET /api/bookings
     */
    @GetMapping
    public Result<List<Booking>> findMyBookings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        return Result.success(bookingService.findByUserId(userId));
    }

    /**
     * 获取当前进行中的骑行
     * GET /api/bookings/current
     * 用于检查用户是否有进行中的骑行（一人一车）
     * 返回所有未结束的订单（PENDING、PAID、ACTIVE??
     */
    @GetMapping(""/current"")
    public Result<Booking> getCurrentRide(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        List<Booking> bookings = bookingService.findByUserId(userId);
        for (Booking b : bookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return Result.success(b);
            }
        }
        return Result.error(""No active ride"");
    }

    /**
     * 获取当前用户未完成的活动订单
     * GET /api/bookings/my/active
     * 返回未结束的所有订单（PENDING、PAID、ACTIVE??
     */
    @GetMapping(""/my/active"")
    public Result<Booking> getMyActiveBooking(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        List<Booking> bookings = bookingService.findByUserId(userId);
        // 返回第一个未结束的订??
        for (Booking b : bookings) {
            String status = b.getStatus();
            // PENDING, PAID, ACTIVE 都是未结束的状??
            if (!""COMPLETED"".equals(status) && !""CANCELLED"".equals(status)) {
                return Result.success(b);
            }
        }
        return Result.error(""No active booking"");
    }

    /**
     * 根据ID获取订单详情
     * GET /api/bookings/{id}
     */
    @GetMapping(""/{id}"")
    public Result<Booking> findById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error(""Booking not found"");
    }

    /**
     * 延长租期
     * PUT /api/bookings/{id}/extend?hireOption=1day
     * 参数: hireOption - 延长的时长选项
     */
    @PutMapping(""/{id}/extend"")
    public Result<Booking> extend(@PathVariable Long id, @RequestParam String hireOption) {
        if (bookingService.extendBooking(id, hireOption)) {
            // 返回更新后的订单信息
            Booking updatedBooking = bookingService.findById(id);
            return Result.success(updatedBooking);
        }
        return Result.error(""Failed to extend booking"");
    }

    /**
     * 取消订单
     * POST /api/bookings/{id}/cancel
     */
    @PostMapping(""/{id}/cancel"")
    public Result<String> cancel(@PathVariable Long id) {
        if (bookingService.cancelBooking(id)) {
            return Result.success(""Booking cancelled successfully"");
        }
        return Result.error(""Failed to cancel booking"");
    }

    /**
     * 还车（结束骑行）
     * POST /api/bookings/{id}/return
     */
    @PostMapping(""/{id}/return"")
    public Result<String> returnScooter(@PathVariable Long id) {
        if (bookingService.returnScooter(id)) {
            return Result.success(""Scooter returned successfully"");
        }
        return Result.error(""Failed to return scooter"");
    }

    /**
     * 支付订单
     * POST /api/bookings/{id}/pay
     * 参数: cardLast4, amount, paymentMethod (可??
     */
    @PostMapping(""/{id}/pay"")
    public Result<String> pay(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> paymentData) {
        if (bookingService.payBooking(id)) {
            return Result.success(""Payment successful"");
        }
        return Result.error(""Payment failed"");
    }

    /**
     * 获取订单确认信息（生成确认码等）
     * GET /api/bookings/{id}/confirmation
     */
    @GetMapping(""/{id}/confirmation"")
    public Result<Map<String, Object>> getConfirmation(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            return Result.error(""Booking not found"");
        }
        Map<String, Object> confirmation = new HashMap<>();
        confirmation.put(""confirmationCode"", booking.getConfirmationCode());
        confirmation.put(""scooterId"", booking.getScooterId());
        confirmation.put(""hireOption"", booking.getHireOption());
        confirmation.put(""startTime"", booking.getStartTime());
        confirmation.put(""endTime"", booking.getEndTime());
        confirmation.put(""totalCost"", booking.getTotalCost());
        confirmation.put(""status"", booking.getStatus());
        return Result.success(confirmation);
    }
}
