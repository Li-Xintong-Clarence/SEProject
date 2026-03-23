package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Pricing;
import com.example.demo.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 价格配置控制器
 * 处理租赁价格的增删改查操作
 * 路径: /api/pricing/*
 */
@RestController
@RequestMapping("/api/pricing")
@CrossOrigin
public class PricingController {

    @Autowired
    private PricingService pricingService;

    /**
     * 获取所有价格配置
     * GET /api/pricing
     */
    @GetMapping
    public Result<List<Pricing>> findAll() {
        return Result.success(pricingService.findAll());
    }

    /**
     * 根据ID获取价格配置
     * GET /api/pricing/{id}
     */
    @GetMapping("/{id}")
    public Result<Pricing> findById(@PathVariable Long id) {
        Pricing pricing = pricingService.findById(id);
        if (pricing != null) {
            return Result.success(pricing);
        }
        return Result.error("Pricing not found");
    }

    /**
     * 添加新价格配置（管理员）
     * POST /api/pricing
     */
    @PostMapping
    public Result<String> add(@RequestBody Pricing pricing) {
        if (pricingService.save(pricing)) {
            return Result.success("Pricing created successfully");
        }
        return Result.error("Failed to create pricing");
    }

    /**
     * 更新价格配置（管理员）
     * PUT /api/pricing
     */
    @PutMapping
    public Result<String> update(@RequestBody Pricing pricing) {
        if (pricingService.update(pricing)) {
            return Result.success("Pricing updated successfully");
        }
        return Result.error("Failed to update pricing");
    }

    /**
     * 删除价格配置（管理员）
     * DELETE /api/pricing/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (pricingService.deleteById(id)) {
            return Result.success("Pricing deleted successfully");
        }
        return Result.error("Failed to delete pricing");
    }
}
