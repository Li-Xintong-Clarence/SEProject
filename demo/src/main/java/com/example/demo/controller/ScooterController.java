package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Scooter;
import com.example.demo.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 滑板车控制器
 * 处理滑板车管理相关的API请求（ID16：管理员配置滑板车）
 * 包括查询滑板车、添加滑板车、更新滑板车信息、删除滑板车等操作
 */
@RestController
@RequestMapping("/api/scooters")
@CrossOrigin
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    /**
     * 获取所有滑板车列表
     * @return 所有滑板车的列表
     */
    @GetMapping
    public Result<List<Scooter>> findAll() {
        return Result.success(scooterService.findAll());
    }

    /**
     * 获取所有可用的滑板车（状态为AVAILABLE）
     * @return 可用滑板车列表
     */
    @GetMapping("/available")
    public Result<List<Scooter>> findAvailable() {
        return Result.success(scooterService.findAvailable());
    }

    /**
     * 根据滑板车ID查询滑板车详情
     * @param id 滑板车ID
     * @return 滑板车详情
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
     * 添加新的滑板车（管理员功能）
     * @param scooter 滑板车信息
     * @return 创建成功返回成功信息，失败返回错误信息
     */
    @PostMapping
    public Result<String> add(@RequestBody Scooter scooter) {
        if (scooterService.save(scooter)) {
            return Result.success("Scooter created successfully");
        }
        return Result.error("Failed to create scooter");
    }

    /**
     * 更新滑板车信息
     * @param scooter 滑板车信息（包含ID）
     * @return 更新成功返回成功信息，失败返回错误信息
     */
    @PutMapping
    public Result<String> update(@RequestBody Scooter scooter) {
        if (scooterService.update(scooter)) {
            return Result.success("Scooter updated successfully");
        }
        return Result.error("Failed to update scooter");
    }

    /**
     * 删除滑板车
     * @param id 滑板车ID
     * @return 删除成功返回成功信息，失败返回错误信息
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (scooterService.deleteById(id)) {
            return Result.success("Scooter deleted successfully");
        }
        return Result.error("Failed to delete scooter");
    }

    /**
     * Backlog ID16: Admin - configure scooter details.
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Scooter scooter = scooterService.findById(id);
        if (scooter == null) {
            return Result.error("Scooter not found");
        }
        scooter.setStatus(status);
        if (scooterService.update(scooter)) {
            return Result.success("Scooter status updated to " + status);
        }
        return Result.error("Failed to update scooter status");
    }
}
