package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.OvertimeFee;
import com.example.demo.service.OvertimeFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 超时费用配置控制器
 * 处理超时费用的增删改查操作
 * 路径: /api/overtime-fee/*
 */
@RestController
@RequestMapping("/api/overtime-fee")
@CrossOrigin
public class OvertimeFeeController {

    @Autowired
    private OvertimeFeeService overtimeFeeService;

    /**
     * 获取所有超时费用配置
     * GET /api/overtime-fee
     */
    @GetMapping
    public Result<List<OvertimeFee>> findAll() {
        return Result.success(overtimeFeeService.findAll());
    }

    /**
     * 根据ID获取超时费用配置
     * GET /api/overtime-fee/{id}
     */
    @GetMapping("/{id}")
    public Result<OvertimeFee> findById(@PathVariable Long id) {
        OvertimeFee overtimeFee = overtimeFeeService.findById(id);
        if (overtimeFee != null) {
            return Result.success(overtimeFee);
        }
        return Result.error("Overtime fee not found");
    }

    /**
     * 添加超时费用配置（管理员）
     * POST /api/overtime-fee
     */
    @PostMapping
    public Result<String> add(@RequestBody OvertimeFee overtimeFee) {
        if (overtimeFeeService.save(overtimeFee)) {
            return Result.success("Overtime fee created successfully");
        }
        return Result.error("Failed to create overtime fee or already exists");
    }

    /**
     * 更新超时费用配置（管理员）
     * PUT /api/overtime-fee
     */
    @PutMapping
    public Result<String> update(@RequestBody OvertimeFee overtimeFee) {
        if (overtimeFeeService.update(overtimeFee)) {
            return Result.success("Overtime fee updated successfully");
        }
        return Result.error("Failed to update overtime fee");
    }

    /**
     * 删除超时费用配置（管理员）
     * DELETE /api/overtime-fee/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (overtimeFeeService.deleteById(id)) {
            return Result.success("Overtime fee deleted successfully");
        }
        return Result.error("Failed to delete overtime fee");
    }
}
