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
 * ��������ʵ��??
 * ʵ�ֶ��������ޣ���صľ���ҵ���߼�
 * ��������������֧����ȡ�������ڡ�ͳ�Ƶȹ���
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
     * ͨ������㴴���������Զ����䳵��??
     * 1. ����û��Ƿ��н����еĶ���
     * 2. ��������Ƿ��п��ó�??
     * 3. �����һ�����ó�??
     * 4. ��������
     */
    @Override
    @Transactional
    public Booking createByDepot(Long userId, Long depotId, String hireOption) {
        // ����û��Ƿ��н����еĶ���
        List<Booking> activeBookings = bookingMapper.findByUserId(userId);
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                return null;
            }
        }

        // ���Ҹ÷����Ŀ��ó�??
        Scooter scooter = scooterService.findFirstAvailableByDepotId(depotId);
        if (scooter == null) {
            return null; // û�п��ó���
        }

        // ��������
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setScooterId(scooter.getId());
        booking.setStartDepotId(depotId);
        booking.setHireOption(hireOption);
        booking.setStatus(""PENDING"");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // ����۸�
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
     * �����¶�����ָ������??
     * 1. ����û��Ƿ��н����еĶ�����һ��һ�����ƣ�
     * 2. ����hireOption��ȡ�۸�
     * 3. ���ö���״̬ΪPENDING
     * 4. ����ȷ��??
     */
    @Override
    public boolean save(Booking booking) {
        logger.info(""=== BookingService.save() ��??===""");
        logger.info(""�û�ID: {}, ���峵ID: {}, ����ѡ��: {}"",
                    booking.getUserId(), booking.getScooterId(), booking.getHireOption());

        // һ��һ�����ƣ�����û��Ƿ��н����еĶ���
        List<Booking> activeBookings = bookingMapper.findByUserId(booking.getUserId());
        logger.info(""���û����ж�����: {}"", activeBookings.size());
        for (Booking b : activeBookings) {
            if (""PAID"".equals(b.getStatus()) || ""ACTIVE"".equals(b.getStatus())) {
                logger.warn(""�û����н����еĶ���������ID: {}, ״?? {}"", b.getId(), b.getStatus());
                return false; // �û����н����еĶ���
            }
        }

        Pricing pricing = pricingMapper.findById(getPricingIdByOption(booking.getHireOption()));
        logger.info(""��ȡ���ļ۸���Ϣ: {}"", pricing);

        if (pricing != null) {
            // ��ȡ�û���Ϣ�����ۿۼ���
            User bookingUser = userMapper.findById(booking.getUserId());
            BigDecimal originalPrice = pricing.getPrice();
            // �����ۺ�۸�
            double finalPrice = discountService.calculateDiscountedPrice(originalPrice.doubleValue(),
                    bookingUser != null ? bookingUser.getUserType() : ""NORMAL"");
            booking.setTotalCost(BigDecimal.valueOf(finalPrice));
            logger.info(""������?? {} -> {}"", originalPrice, finalPrice);
        }
        booking.setStatus(""PENDING"");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setConfirmationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        logger.info(""׼�����붩��: userId={}, scooterId={}, status={}, totalCost={}"",
                    booking.getUserId(), booking.getScooterId(), booking.getStatus(), booking.getTotalCost());

        int result = bookingMapper.insert(booking);
        logger.info(""������: {}, �¶���ID: {}"", result, booking.getId());

        if (result > 0) {
            logger.info(""=== BookingService.save() �ɹ� ==="");
        } else {
            logger.error(""=== BookingService.save() ʧ�ܣ�insert����0 ==="");
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
     * �ӳ�����
     * 1. ���Ҷ��������״̬������ACTIVE
     * 2. �����µĽ���ʱ��
     * 3. ������Ӧ����
     */
    @Override
    public boolean extendBooking(Long id, String hireOption) {
        Booking booking = bookingMapper.findById(id);
        // ���� PAID ??ACTIVE ״̬�Ķ����ӳ�
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
     * ȡ������
     * 1. ��鶩��״̬������������ɻ���ȡ��??
     * 2. ����״̬ΪCANCELLED
     * 3. �ͷų�����״̬�Ļ�AVAILABLE??
     * 4. ����ȡ����??
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
     * �������������У�
     * 1. ��鶩��״̬������PAID��ACTIVE
     * 2. ����״̬ΪCOMPLETED
     * 3. �ͷų�����״̬�Ļ�AVAILABLE??
     * 4. ���ͽ�����??
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
     * ֧������
     * 1. ��鶩��״̬������PENDING
     * 2. ����״̬ΪPAID�����ÿ�ʼ�ͽ���ʱ��
     * 3. ���³���״̬ΪIN_USE��ʹ����??
     * 4. ����ȷ����??
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
     * ����Ԥ��ȷ����??
     * ������ȷ���롢������š�����ѡ�ʱ�䡢�ܷ�??
     */
    private void sendConfirmationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println(""����ȷ���ʼ�ʧ�ܣ��Ҳ����û���userId="" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println(""����ȷ���ʼ�ʧ�ܣ��û�����Ϊ�գ�userId="" + booking.getUserId());
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
            System.err.println(""����ȷ���ʼ���?? "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ����ȡ��Ԥ����??
     */
    private void sendCancellationEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println(""����ȡ���ʼ�ʧ�ܣ��Ҳ����û���userId="" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println(""����ȡ���ʼ�ʧ�ܣ��û�����Ϊ�գ�userId="" + booking.getUserId());
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
            System.err.println(""����ȡ���ʼ���?? "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ���ͽ���������??
     */
    private void sendCompletionEmail(Booking booking) {
        try {
            User user = userMapper.findById(booking.getUserId());
            if (user == null) {
                System.err.println(""���ͽ����ʼ�ʧ�ܣ��Ҳ����û���userId="" + booking.getUserId());
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println(""���ͽ����ʼ�ʧ�ܣ��û�����Ϊ�գ�userId="" + booking.getUserId());
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
            System.err.println(""���ͽ����ʼ���?? "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ��������ѡ���ȡ�۸�ID
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
     * �������ʱ��
     * ��������ѡ��������ڽ���ʱ��
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
     * ��ȡ�û�ͳ����Ϣ
     * ���أ����������������ѽ�������ʱ??
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
