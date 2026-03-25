package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Scooter;
import com.example.demo.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 电动车控制器
 * 处理电动车（共享单车/滑板车）的增删改查操作
 * 路径: /api/scooters/*
 */
@RestController
@RequestMapping("/api/scooters")
@CrossOrigin
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    /**
     * 获取所有电动车列表
     * GET /api/scooters
     */
    @GetMapping
    public Result<List<Scooter>> findAll() {
        return Result.success(scooterService.findAll());
    }

    /**
     * 获取可用的电动车列表
     * GET /api/scooters/available
     * 返回状态为AVAILABLE的车辆
     */
    @GetMapping("/available")
    public Result<List<Scooter>> findAvailable() {
        return Result.success(scooterService.findAvailable());
    }

    /**
     * 根据ID获取电动车信息
     * GET /api/scooters/{id}
     */
    @GetMapping("/{id}")
    public Result<Scooter> findById(@PathVariable Long id) {
        Scooter scooter = scooterService.findById(id);
        if (scooter != null) {
            return Result.success(scooter);
        }
        return Result.error("Scooter not found");
    }

    /**
     * 添加新电动车（管理员）
     * POST /api/scooters
     */
    @PostMapping
    public Result<String> add(@RequestBody Scooter scooter) {
        if (scooterService.save(scooter)) {
            return Result.success("Scooter created successfully");
        }
        return Result.error("Failed to create scooter");
    }

    /**
     * 更新电动车信息（管理员）
     * PUT /api/scooters
     */
    @PutMapping
    public Result<String> update(@RequestBody Scooter scooter) {
        if (scooterService.update(scooter)) {
            return Result.success("Scooter updated successfully");
        }
        return Result.error("Failed to update scooter");
    }

    /**
     * 删除电动车（管理员）
     * DELETE /api/scooters/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (scooterService.deleteById(id)) {
            return Result.success("Scooter deleted successfully");
        }
        return Result.error("Failed to delete scooter");
    }

    /**
     * 更新电动车状态（管理员）
     * PUT /api/scooters/{id}/status?status=AVAILABLE
     * 状态可选: AVAILABLE（可用）, IN_USE（使用中）, MAINTENANCE（维护中）, RETIRED（已退役）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        if (scooterService.updateStatus(id, status)) {
            return Result.success("Status updated successfully");
        }
        return Result.error("Failed to update status");
    }
}
