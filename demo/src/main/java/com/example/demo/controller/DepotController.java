package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Depot;
import com.example.demo.service.DepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 服务点控制器
 * 处理服务点的查询、管理操作
 * 路径: /api/depots/*
 */
@RestController
@RequestMapping("/api/depots")
@CrossOrigin
public class DepotController {

    @Autowired
    private DepotService depotService;

    /**
     * 获取所有服务点列表（包含车辆统计）
     * GET /api/depots
     */
    @GetMapping
    public Result<List<Map<String, Object>>> findAllWithCount() {
        return Result.success(depotService.getAllDepotsWithScooterCount());
    }

    /**
     * 获取所有活跃服务点（包含车辆统计）
     * GET /api/depots/active
     */
    @GetMapping("/active")
    public Result<List<Map<String, Object>>> findActiveWithCount() {
        return Result.success(depotService.getAllDepotsWithScooterCount());
    }

    /**
     * 根据ID获取服务点详情（包含车辆统计）
     * GET /api/depots/{id}
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> findById(@PathVariable Long id) {
        Map<String, Object> result = depotService.getDepotWithScooterCount(id);
        if (result != null) {
            return Result.success(result);
        }
        return Result.error("Depot not found");
    }

    /**
     * 获取有可用车辆的服务点
     * GET /api/depots/available
     */
    @GetMapping("/available")
    public Result<List<Map<String, Object>>> findAvailableDepots() {
        List<Map<String, Object>> allDepots = depotService.getAllDepotsWithScooterCount();
        List<Map<String, Object>> availableDepots = allDepots.stream()
                .filter(d -> (Long) d.get("availableCount") > 0)
                .collect(java.util.stream.Collectors.toList());
        return Result.success(availableDepots);
    }
}
