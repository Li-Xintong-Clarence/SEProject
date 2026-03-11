package com.example.demo.service.impl;

import com.example.demo.entity.Booking;
import com.example.demo.entity.HireOption;
import com.example.demo.entity.Scooter;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.HireOptionMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.service.BookingService;
import com.example.demo.vo.BookingRequest;
import com.example.demo.vo.DailyIncomeResponse;
import com.example.demo.vo.WeeklyIncomeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 预订服务实现类
 * 实现预订相关的业务逻辑
 */
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private HireOptionMapper hireOptionMapper;
    @Autowired
    private ScooterMapper scooterMapper;

    /**
     * 创建预订
     * 1. 验证滑板车存在且可用
     * 2. 获取租用选项信息
     * 3. 计算预订结束时间
     * 4. 创建预订记录
     * 5. 更新滑板车状态为使用中
     */
    @Override
    @Transactional
    public Booking createBooking(Long userId, BookingRequest request) {
        // 1. Verify scooter exists and is available
        Scooter scooter = scooterMapper.findById(request.getScooterId());
        if (scooter == null) {
            throw new RuntimeException("Scooter not found");
        }
        if (!"AVAILABLE".equals(scooter.getStatus())) {
            throw new RuntimeException("Scooter is not available for booking");
        }

        // 2. Get hire option details
        List<HireOption> options = hireOptionMapper.findAllActive();
        HireOption selectedOption = options.stream()
                .filter(o -> o.getCode().equals(request.getHireOptionCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid hire option"));

        // 3. Calculate booking times
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledEnd = now.plusMinutes(selectedOption.getDurationMinutes());

        // 4. Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setScooterId(request.getScooterId());
        booking.setHireOptionCode(selectedOption.getCode());
        booking.setStartTime(now);
        booking.setScheduledEndTime(scheduledEnd);
        booking.setCost(selectedOption.getPrice());
        booking.setStatus("ACTIVE");
        booking.setCreatedAt(now);

        bookingMapper.insert(booking);

        // 5. Update scooter status to IN_USE
        scooter.setStatus("IN_USE");
        scooterMapper.update(scooter);

        return booking;
    }

    /**
     * 根据ID查询预订
     */
    @Override
    public Booking findById(Long id) {
        return bookingMapper.findById(id);
    }

    /**
     * 查询用户的所有预订
     */
    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookingMapper.findByUserId(userId);
    }

    /**
     * 查询所有预订
     */
    @Override
    public List<Booking> findAll() {
        return bookingMapper.findAll();
    }

    /**
     * 取消预订
     * 1. 验证预订存在
     * 2. 验证用户权限
     * 3. 验证预订状态为ACTIVE
     * 4. 更新预订状态为CANCELLED
     * 5. 更新滑板车状态为AVAILABLE
     */
    @Override
    @Transactional
    public boolean cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingMapper.findById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }
        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new RuntimeException("Only active bookings can be cancelled");
        }

        // Update booking status to CANCELLED
        bookingMapper.updateStatus(bookingId, "CANCELLED");

        // Update scooter status back to AVAILABLE
        Scooter scooter = scooterMapper.findById(booking.getScooterId());
        if (scooter != null) {
            scooter.setStatus("AVAILABLE");
            scooterMapper.update(scooter);
        }

        return true;
    }

    /**
     * 延长预订时间
     * 1. 验证预订存在
     * 2. 验证用户权限
     * 3. 验证预订状态为ACTIVE
     * 4. 获取新的租用选项
     * 5. 计算新的结束时间和费用
     * 6. 更新预订信息
     */
    @Override
    @Transactional
    public boolean extendBooking(Long bookingId, Long userId, String hireOptionCode) {
        Booking booking = bookingMapper.findById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to extend this booking");
        }
        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new RuntimeException("Only active bookings can be extended");
        }

        // Get new hire option
        List<HireOption> options = hireOptionMapper.findAllActive();
        HireOption newOption = options.stream()
                .filter(o -> o.getCode().equals(hireOptionCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid hire option"));

        // Calculate new end time
        LocalDateTime currentEnd = booking.getScheduledEndTime();
        LocalDateTime newEnd = currentEnd.plusMinutes(newOption.getDurationMinutes());

        // Add cost difference
        BigDecimal additionalCost = newOption.getPrice().subtract(booking.getCost());
        booking.setCost(booking.getCost().add(additionalCost));
        booking.setScheduledEndTime(newEnd);
        booking.setHireOptionCode(hireOptionCode);

        bookingMapper.update(booking);
        return true;
    }

    /**
     * 获取日收入统计
     * 返回过去7天每天的收入数据
     */
    @Override
    public DailyIncomeResponse getDailyIncome() {
        LocalDateTime endDate = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startDate = endDate.minusDays(7);

        String startStr = startDate.toString().replace("T", " ");
        String endStr = endDate.toString().replace("T", " ");

        List<Map<String, Object>> results = bookingMapper.findDailyIncome(startStr, endStr);

        Map<String, BigDecimal> dailyIncome = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (LocalDate d = startDate.toLocalDate(); !d.isAfter(endDate.minusDays(1).toLocalDate()); d = d.plusDays(1)) {
            String dateStr = d.toString();
            BigDecimal dayTotal = BigDecimal.ZERO;
            for (Map<String, Object> row : results) {
                Object dateObj = row.get("bookingDate");
                if (dateObj != null && dateObj.toString().equals(dateStr)) {
                    dayTotal = new BigDecimal(row.get("totalCost").toString());
                    break;
                }
            }
            dailyIncome.put(dateStr, dayTotal);
            total = total.add(dayTotal);
        }

        DailyIncomeResponse response = new DailyIncomeResponse();
        response.setStartDate(startDate.toLocalDate().toString());
        response.setEndDate(endDate.minusDays(1).toLocalDate().toString());
        response.setDailyIncome(dailyIncome);
        response.setTotalIncome(total);
        return response;
    }

    /**
     * 获取周收入统计（按租用选项分组）
     * 返回过去7天按租用选项统计的收入数据
     */
    @Override
    public WeeklyIncomeResponse getWeeklyIncome() {
        // Get last 7 days
        LocalDateTime endDate = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startDate = endDate.minusDays(7);

        String startStr = startDate.toString().replace("T", " ");
        String endStr = endDate.toString().replace("T", " ");

        List<Map<String, Object>> results = bookingMapper.findWeeklyIncomeByOption(startStr, endStr);

        Map<String, BigDecimal> incomeByOption = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map<String, Object> row : results) {
            String code = (String) row.get("hireOptionCode");
            BigDecimal cost = row.get("totalCost") != null ? new BigDecimal(row.get("totalCost").toString()) : BigDecimal.ZERO;
            incomeByOption.put(code, cost);
            total = total.add(cost);
        }

        WeeklyIncomeResponse response = new WeeklyIncomeResponse();
        response.setStartDate(startDate.toLocalDate().toString());
        response.setEndDate(endDate.minusDays(1).toLocalDate().toString());
        response.setIncomeByOption(incomeByOption);
        response.setTotalIncome(total);

        return response;
    }
}
