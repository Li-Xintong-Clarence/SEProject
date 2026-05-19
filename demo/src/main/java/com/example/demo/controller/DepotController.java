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

    /**
     * 创建服务点
     * POST /api/depots
     */
    @PostMapping
    public Result<Depot> create(@RequestBody Depot depot) {
        if (depot.getStatus() == null) {
            depot.setStatus("ACTIVE");
        }
        if (depot.getDepotNumber() == null || depot.getDepotNumber().isEmpty()) {
            return Result.error("服务点编号不能为空");
        }
        // 检查编号是否已存在
        Depot existing = depotService.findByNumber(depot.getDepotNumber());
        if (existing != null) {
            return Result.error("服务点编号已存在");
        }
        boolean success = depotService.save(depot);
        if (success) {
            return Result.success(depot);
        }
        return Result.error("创建失败");
    }

    /**
     * 更新服务点
     * PUT /api/depots/{id}
     */
    @PutMapping("/{id}")
    public Result<Depot> update(@PathVariable Long id, @RequestBody Depot depot) {
        depot.setId(id);
        // 检查编号是否与其他服务点冲突
        Depot existing = depotService.findByNumber(depot.getDepotNumber());
        if (existing != null && !existing.getId().equals(id)) {
            return Result.error("服务点编号已存在");
        }
        boolean success = depotService.update(depot);
        if (success) {
            return Result.success(depot);
        }
        return Result.error("更新失败");
    }

    /**
     * 删除服务点
     * DELETE /api/depots/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 检查该服务点下是否有车辆
        Map<String, Object> depotInfo = depotService.getDepotWithScooterCount(id);
        if (depotInfo != null && (Long) depotInfo.getOrDefault("totalScooters", 0L) > 0) {
            return Result.error("该服务点下仍有车辆，无法删除");
        }
        boolean success = depotService.deleteById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
