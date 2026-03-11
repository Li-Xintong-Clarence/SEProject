package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Booking;
import com.example.demo.entity.User;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import com.example.demo.vo.BookingRequest;
import com.example.demo.vo.DailyIncomeResponse;
import com.example.demo.vo.WeeklyIncomeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 预订控制器
 * 处理滑板车租赁预订相关的API请求（ID5：预订滑板车）
 * 包括创建预订、查询预订、取消预订、延长预订等操作
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;

    /**
     * 创建预订（ID5：预订滑板车）
     * 需要用户登录（通过Authorization header传递JWT令牌）
     * @param request 预订请求，包含滑板车ID和租用选项代码
     * @param token JWT令牌（格式：Bearer xxx）
     * @return 预订成功返回预订信息，失败返回错误信息
     */
    @PostMapping
    public Result<Booking> create(@RequestBody BookingRequest request,
                                  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("Unauthorized");
        }
        String username = token.replace("Bearer ", "");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        try {
            Booking booking = bookingService.createBooking(user.getId(), request);
            return Result.success(booking);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据预订ID查询预订详情
     * @param id 预订ID
     * @return 预订详情
     */
    @GetMapping("/{id}")
    public Result<Booking> getById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return Result.success(booking);
        }
        return Result.error("Booking not found");
    }

    /**
     * 查询当前登录用户的所有预订记录
     * 需要用户登录
     * @param token JWT令牌（格式：Bearer xxx）
     * @return 当前用户的所有预订列表
     */
    @GetMapping("/my")
    public Result<List<Booking>> myBookings(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("Unauthorized");
        }
        String username = token.replace("Bearer ", "");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        return Result.success(bookingService.findByUserId(user.getId()));
    }

    /**
     * 查询所有预订记录（管理员功能）
     * @return 所有预订列表
     */
    @GetMapping
    public Result<List<Booking>> findAll() {
        return Result.success(bookingService.findAll());
    }

    /**
     * 取消预订
     * 需要用户登录，只能取消自己创建的预订且状态为ACTIVE的预订
     * @param id 预订ID
     * @param token JWT令牌（格式：Bearer xxx）
     * @return 取消成功返回成功信息，失败返回错误信息
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("Unauthorized");
        }
        String username = token.replace("Bearer ", "");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        try {
            bookingService.cancelBooking(id, user.getId());
            return Result.success("Booking cancelled successfully");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看周收入统计（ID19：按租用选项查看周收入）
     * 返回过去7天的收入，按租用选项分组统计
     * @return 周收入统计数据
     */
    @GetMapping("/weekly-income")
    public Result<WeeklyIncomeResponse> weeklyIncome() {
        return Result.success(bookingService.getWeeklyIncome());
    }

    /**
     * 延长预订时间（ID11：延长当前预订）
     * 需要用户登录，只能延长自己创建的且状态为ACTIVE的预订
     * @param id 预订ID
     * @param hireOptionCode 新的租用选项代码（如从1HR续到4HR）
     * @param token JWT令牌（格式：Bearer xxx）
     * @return 延长成功返回成功信息，失败返回错误信息
     */
    @PostMapping("/{id}/extend")
    public Result<String> extend(@PathVariable Long id,
                                 @RequestParam String hireOptionCode,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("Unauthorized");
        }
        String username = token.replace("Bearer ", "");
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        try {
            bookingService.extendBooking(id, user.getId(), hireOptionCode);
            return Result.success("Booking extended successfully");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
