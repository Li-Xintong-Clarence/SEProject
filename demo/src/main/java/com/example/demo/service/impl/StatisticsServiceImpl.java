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
            Number hourNum = (Number) item.get("hour");
            if (hourNum != null) {
                int hour = hourNum.intValue();
                int count = ((Number) item.get("count")).intValue();
                hoursMap.put(hour + "时", count);
            }
        }

        result.put("peakHours", hoursMap);
        return result;
    }

    // ID20: 一周七天的中英文映射
    private static final String[] DAY_NAMES = {
        "周日", "周一", "周二", "周三", "周四", "周五", "周六"
    };

    @Override
    public Map<String, Object> getWeeklyHotDays() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dayList = bookingMapper.getBookingsByDayOfWeek();

        // 初始化一周七天
        Map<String, Integer> hotDaysMap = new LinkedHashMap<>();
        hotDaysMap.put("周日", 0);
        hotDaysMap.put("周一", 0);
        hotDaysMap.put("周二", 0);
        hotDaysMap.put("周三", 0);
        hotDaysMap.put("周四", 0);
        hotDaysMap.put("周五", 0);
        hotDaysMap.put("周六", 0);

        // 填充数据
        // MySQL DAYOFWEEK: 1=周日, 2=周一, ..., 7=周六
        for (Map<String, Object> item : dayList) {
            Number dayNum = (Number) item.get("day_of_week");
            Integer count = ((Number) item.get("count")).intValue();
            if (dayNum != null) {
                int dayIndex = dayNum.intValue() - 1; // 转为0-based索引
                if (dayIndex >= 0 && dayIndex < 7) {
                    hotDaysMap.put(DAY_NAMES[dayIndex], count);
                }
            }
        }

        result.put("hotDays", hotDaysMap);

        // 找出最热门的日期
        String hottestDay = "无数据";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : hotDaysMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                hottestDay = entry.getKey();
            }
        }
        result.put("hottestDay", hottestDay);
        result.put("hottestDayCount", maxCount);

        return result;
    }

    @Override
    public Map<String, Object> getDailyIncomeBreakdown() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dataList = bookingMapper.getDailyIncomeByOption();

        // 初始化最近7天的数据
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Map<String, Map<String, Double>> dailyBreakdown = new LinkedHashMap<>();

        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DAY_OF_YEAR, -i);
            String dateKey = sdf.format(day.getTime());
            Map<String, Double> optionBreakdown = new LinkedHashMap<>();
            optionBreakdown.put("1hr", 0.0);
            optionBreakdown.put("4hr", 0.0);
            optionBreakdown.put("1day", 0.0);
            optionBreakdown.put("1week", 0.0);
            optionBreakdown.put("total", 0.0);
            dailyBreakdown.put(dateKey, optionBreakdown);
        }

        // 填充数据
        for (Map<String, Object> item : dataList) {
            Date date = (Date) item.get("date");
            String hireOption = (String) item.get("hire_option");
            Double income = item.get("income") != null ? ((Number) item.get("income")).doubleValue() : 0.0;

            if (date != null && hireOption != null) {
                String dateKey = sdf.format(date);
                Map<String, Double> optionBreakdown = dailyBreakdown.get(dateKey);
                if (optionBreakdown != null) {
                    optionBreakdown.put(hireOption, income);
                    optionBreakdown.put("total", optionBreakdown.get("total") + income);
                }
            }
        }

        result.put("dailyBreakdown", dailyBreakdown);

        // 计算各租期的总收入
        Map<String, Double> totalByOption = new LinkedHashMap<>();
        totalByOption.put("1hr", 0.0);
        totalByOption.put("4hr", 0.0);
        totalByOption.put("1day", 0.0);
        totalByOption.put("1week", 0.0);

        for (Map<String, Double> optionBreakdown : dailyBreakdown.values()) {
            for (String option : new String[]{"1hr", "4hr", "1day", "1week"}) {
                totalByOption.put(option, totalByOption.get(option) + optionBreakdown.get(option));
            }
        }
        result.put("totalByOption", totalByOption);

        return result;
    }
}
