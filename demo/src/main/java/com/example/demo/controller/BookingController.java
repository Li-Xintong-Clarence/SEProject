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
 * ��������??
 * �������޶����Ĵ�������ѯ��֧����ȡ�������ڵȲ���
 * ·��: /api/bookings/*
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
     * �����¶�����ͨ��������⳵���Զ����䳵��??
     * POST /api/bookings/depot
     * ����: depotId, hireOption
     */
    @PostMapping(""depot"")
    public Result<Booking> createByDepot(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");

        // һ��һ�����ƣ�����û��Ƿ��н����еĶ���
        List<Booking> activeBookings = bookingService.findByUserId(userId);
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return Result.error(""�������ڽ����е��г̣�������ɻ�ȡ�����ٴ����¶�??"");
            }
        }

        Long depotId = Long.parseLong(params.get(""depotId""));
        String hireOption = params.get(""hireOption"");

        Booking booking = bookingService.createByDepot(userId, depotId, hireOption);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error(""�÷�������޿��ó���"");
    }

    /**
     * �����¶�����ָ������ID??
     * POST /api/bookings
     * ����: scooterId, hireOption, startTime ??
     */
    @PostMapping
    public Result<Booking> create(@RequestBody Booking booking, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        booking.setUserId(userId);

        // һ��һ�����ƣ�����û��Ƿ��н����еĶ���
        List<Booking> activeBookings = bookingService.findByUserId(userId);
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return Result.error(""�������ڽ����е��г̣�������:"" + b.getId() + ""����������ɻ�ȡ�����ٴ����¶���"");
            }
        }

        // ����Ҫ��??
        if (booking.getScooterId() == null) {
            return Result.error(""���峵ID����Ϊ��"");
        }
        if (booking.getHireOption() == null || booking.getHireOption().isEmpty()) {
            return Result.error(""��ѡ������ʱ��"");
        }

        // ���û��ָ�� startDepotId���Զ��ӻ��峵��??
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
        return Result.error(""��������ʧ�ܣ����Ժ�����"");
    }

    /**
     * ��ȡ��ǰ�û��Ķ�����??
     * GET /api/bookings
     */
    @GetMapping
    public Result<List<Booking>> findMyBookings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        return Result.success(bookingService.findByUserId(userId));
    }

    /**
     * ��ȡ��ǰ�����е�����
     * GET /api/bookings/current
     * ���ڼ���û��Ƿ��н����е����У�һ��һ����
     * ��������δ�����Ķ�����PENDING��PAID��ACTIVE??
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
     * ��ȡ��ǰ�û�δ��ɵĻ����
     * GET /api/bookings/my/active
     * ����δ���������ж�����PENDING��PAID��ACTIVE??
     */
    @GetMapping(""/my/active"")
    public Result<Booking> getMyActiveBooking(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(""userId"");
        List<Booking> bookings = bookingService.findByUserId(userId);
        // ���ص�һ��δ�����Ķ�??
        for (Booking b : bookings) {
            String status = b.getStatus();
            // PENDING, PAID, ACTIVE ����δ������״??
            if (!""COMPLETED"".equals(status) && !""CANCELLED"".equals(status)) {
                return Result.success(b);
            }
        }
        return Result.error(""No active booking"");
    }

    /**
     * ����ID��ȡ��������
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
     * �ӳ�����
     * PUT /api/bookings/{id}/extend?hireOption=1day
     * ����: hireOption - �ӳ���ʱ��ѡ��
     */
    @PutMapping(""/{id}/extend"")
    public Result<Booking> extend(@PathVariable Long id, @RequestParam String hireOption) {
        if (bookingService.extendBooking(id, hireOption)) {
            // ���ظ��º�Ķ�����Ϣ
            Booking updatedBooking = bookingService.findById(id);
            return Result.success(updatedBooking);
        }
        return Result.error(""Failed to extend booking"");
    }

    /**
     * ȡ������
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
     * �������������У�
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
     * ֧������
     * POST /api/bookings/{id}/pay
     * ����: cardLast4, amount, paymentMethod (��??
     */
    @PostMapping(""/{id}/pay"")
    public Result<String> pay(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> paymentData) {
        if (bookingService.payBooking(id)) {
            return Result.success(""Payment successful"");
        }
        return Result.error(""Payment failed"");
    }

    /**
     * ��ȡ����ȷ����Ϣ������ȷ����ȣ�
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
