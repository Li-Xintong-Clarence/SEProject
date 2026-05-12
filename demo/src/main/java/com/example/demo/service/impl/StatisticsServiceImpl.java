package com.example.demo.service.impl;

import com.example.demo.mapper.*;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScooterMapper scooterMapper;

    @Autowired
    private DepotMapper depotMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 用户统计
        int totalUsers = userMapper.countUsers();
        overview.put("totalUsers", totalUsers);

        // 车辆统计
        int totalScooters = scooterMapper.countScooters();
        int availableScooters = scooterMapper.countByStatus("AVAILABLE");
        int inUseScooters = scooterMapper.countByStatus("IN_USE");
        int maintenanceScooters = scooterMapper.countByStatus("MAINTENANCE");
        overview.put("totalScooters", totalScooters);
        overview.put("availableScooters", availableScooters);
        overview.put("inUseScooters", inUseScooters);
        overview.put("maintenanceScooters", maintenanceScooters);
        overview.put("usageRate", totalScooters > 0 ? Math.round((double) inUseScooters / totalScooters * 100) : 0);

        // 订单统计
        int totalBookings = bookingMapper.countBookings();
        int activeBookings = bookingMapper.countByStatus("ACTIVE") + bookingMapper.countByStatus("PAID");
        overview.put("totalBookings", totalBookings);
        overview.put("activeBookings", activeBookings);

        // 收入统计
        Double totalIncome = bookingMapper.sumTotalIncome();
        Double todayIncome = bookingMapper.sumTodayIncome();
        Double weekIncome = bookingMapper.sumWeekIncome();
        overview.put("totalIncome", totalIncome != null ? totalIncome : 0.0);
        overview.put("todayIncome", todayIncome != null ? todayIncome : 0.0);
        overview.put("weekIncome", weekIncome != null ? weekIncome : 0.0);

        // 服务点统计
        overview.put("totalDepots", depotMapper.countDepots());

        // 工单统计
        int pendingIssues = bookingMapper.countPendingIssues();
        overview.put("pendingIssues", pendingIssues);

        // 订单完成率
        int completedBookings = bookingMapper.countByStatus("COMPLETED");
        int cancelledBookings = bookingMapper.countByStatus("CANCELLED");
        double completionRate = totalBookings > 0 ? (double) completedBookings / totalBookings * 100 : 0;
        overview.put("completionRate", Math.round(completionRate));

        // 平均骑行时长
        Double avgDuration = bookingMapper.avgRideDuration();
        overview.put("avgRideDuration", avgDuration != null ? Math.round(avgDuration) : 0);

        // 平均订单金额
        Double avgOrderValue = totalBookings > 0 && totalIncome != null ? totalIncome / completedBookings : 0;
        overview.put("avgOrderValue", Math.round(avgOrderValue * 100.0) / 100.0);

        return overview;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalUsers = userMapper.countUsers();
        int todayNewUsers = userMapper.countTodayNewUsers();
        int weekNewUsers = userMapper.countWeekNewUsers();
        int monthNewUsers = userMapper.countMonthNewUsers();

        stats.put("totalUsers", totalUsers);
        stats.put("todayNewUsers", todayNewUsers);
        stats.put("weekNewUsers", weekNewUsers);
        stats.put("monthNewUsers", monthNewUsers);

        return stats;
    }

    @Override
    public Map<String, Object> getBookingStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalBookings = bookingMapper.countBookings();
        int completedBookings = bookingMapper.countByStatus("COMPLETED");
        int cancelledBookings = bookingMapper.countByStatus("CANCELLED");
        int activeBookings = bookingMapper.countByStatus("ACTIVE") + bookingMapper.countByStatus("PAID");

        stats.put("totalBookings", totalBookings);
        stats.put("completedBookings", completedBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("activeBookings", activeBookings);

        Double totalIncome = bookingMapper.sumTotalIncome();
        double avgOrderValue = completedBookings > 0 && totalIncome != null ? totalIncome / completedBookings : 0;
        stats.put("avgOrderValue", Math.round(avgOrderValue * 100.0) / 100.0);

        return stats;
    }

    @Override
    public Map<String, Object> getScooterStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalScooters = scooterMapper.countScooters();
        int availableScooters = scooterMapper.countByStatus("AVAILABLE");
        int inUseScooters = scooterMapper.countByStatus("IN_USE");
        int maintenanceScooters = scooterMapper.countByStatus("MAINTENANCE");
        int lowBatteryScooters = scooterMapper.countLowBattery();

        stats.put("totalScooters", totalScooters);
        stats.put("availableScooters", availableScooters);
        stats.put("inUseScooters", inUseScooters);
        stats.put("maintenanceScooters", maintenanceScooters);
        stats.put("lowBatteryScooters", lowBatteryScooters);
        stats.put("usageRate", totalScooters > 0 ? Math.round((double) inUseScooters / totalScooters * 100) : 0);

        return stats;
    }

    @Override
    public Map<String, Object> getDailyIncome() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dailyList = bookingMapper.getDailyIncome();

        Map<String, Double> dailyMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        // 初始化最近7天
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DAY_OF_YEAR, -i);
            String dateKey = sdf.format(day.getTime());
            dailyMap.put(dateKey, 0.0);
        }

        // 填充数据
        for (Map<String, Object> item : dailyList) {
            Date date = (Date) item.get("date");
            Double income = ((Number) item.get("income")).doubleValue();
            if (date != null) {
                String dateKey = sdf.format(date);
                dailyMap.put(dateKey, income);
            }
        }

        result.put("dailyIncome", dailyMap);
        return result;
    }

    @Override
    public Map<String, Object> getWeeklyIncome() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> weeklyData = bookingMapper.getWeeklyIncomeByOption();

        Map<String, Double> incomeByOption = new LinkedHashMap<>();
        for (Map<String, Object> item : weeklyData) {
            String option = (String) item.get("hire_option");
            Double income = ((Number) item.get("income")).doubleValue();
            incomeByOption.put(option, income);
        }

        result.put("incomeByHireOption", incomeByOption);

        Double total = incomeByOption.values().stream().mapToDouble(Double::doubleValue).sum();
        result.put("totalIncome", total);

        return result;
    }

    @Override
    public Map<String, Object> getUserGrowth() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dailyList = userMapper.getDailyNewUsers();

        Map<String, Integer> dailyMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        // 初始化最近7天
        for (int i = 6; i >= 0; i--) {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DAY_OF_YEAR, -i);
            String dateKey = sdf.format(day.getTime());
            dailyMap.put(dateKey, 0);
        }

        // 填充数据
        for (Map<String, Object> item : dailyList) {
            Date date = (Date) item.get("date");
            Integer count = ((Number) item.get("count")).intValue();
            if (date != null) {
                String dateKey = sdf.format(date);
                dailyMap.put(dateKey, count);
            }
        }

        result.put("dailyNewUsers", dailyMap);
        return result;
    }

    @Override
    public Map<String, Object> getTopActiveUsers() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> topUsers = bookingMapper.getTopActiveUsers(10);

        List<Map<String, Object>> userList = new ArrayList<>();
        for (Map<String, Object> item : topUsers) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", item.get("user_id"));
            user.put("username", item.get("username"));
            user.put("email", item.get("email"));
            user.put("bookingCount", ((Number) item.get("booking_count")).intValue());
            userList.add(user);
        }

        result.put("topUsers", userList);
        return result;
    }

    @Override
    public Map<String, Object> getBookingStatusDistribution() {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> statusList = bookingMapper.getBookingStatusDistribution();

        for (Map<String, Object> item : statusList) {
            String status = (String) item.get("status");
            int count = ((Number) item.get("count")).intValue();
            result.put(status, count);
        }

        return result;
    }

    @Override
    public Map<String, Object> getPeakHours() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> hoursList = bookingMapper.getPeakHours();

        Map<String, Integer> hoursMap = new LinkedHashMap<>();
        // 初始化24小时
        for (int i = 0; i < 24; i++) {
            hoursMap.put(i + "时", 0);
        }

        for (Map<String, Object> item : hoursList) {
            int hour = ((Number) item.get("hour")).intValue();
            int count = ((Number) item.get("count")).intValue();
            hoursMap.put(hour + "时", count);
        }

        result.put("peakHours", hoursMap);
        return result;
    }
}
