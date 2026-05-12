package com.example.demo.controller;

import com.example.demo.entity.Hotspot;
import com.example.demo.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 调度控制器
 * 提供滑板车调度相关API
 */
@RestController
@RequestMapping("/api/dispatch")
@CrossOrigin(origins = "*")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    /**
     * 触发调度：根据热门区域重新分布滑板车
     */
    @PostMapping("/redistribute")
    public ResponseEntity<Map<String, Object>> redistribute() {
        Map<String, Object> result = dispatchService.redistributeScooters();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有热门区域
     */
    @GetMapping("/hotspots")
    public ResponseEntity<List<Hotspot>> getHotspots() {
        List<Hotspot> hotspots = dispatchService.getAllHotspots();
        return ResponseEntity.ok(hotspots);
    }

    /**
     * 获取活跃热门区域
     */
    @GetMapping("/hotspots/active")
    public ResponseEntity<List<Hotspot>> getActiveHotspots() {
        List<Hotspot> hotspots = dispatchService.getActiveHotspots();
        return ResponseEntity.ok(hotspots);
    }

    /**
     * 添加热门区域
     */
    @PostMapping("/hotspots")
    public ResponseEntity<Map<String, Object>> addHotspot(@RequestBody Hotspot hotspot) {
        boolean success = dispatchService.addHotspot(hotspot);
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "添加成功" : "添加失败"
        ));
    }

    /**
     * 更新热门区域权重
     */
    @PutMapping("/hotspots/{id}/weight")
    public ResponseEntity<Map<String, Object>> updateWeight(
            @PathVariable Long id, 
            @RequestParam Integer weight) {
        boolean success = dispatchService.updateHotspotWeight(id, weight);
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "更新成功" : "更新失败"
        ));
    }

    /**
     * 删除热门区域
     */
    @DeleteMapping("/hotspots/{id}")
    public ResponseEntity<Map<String, Object>> deleteHotspot(@PathVariable Long id) {
        boolean success = dispatchService.deleteHotspot(id);
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "删除成功" : "删除失败"
        ));
    }

    /**
     * 模拟使用（增加区域权重）
     */
    @PostMapping("/hotspots/{id}/simulate-usage")
    public ResponseEntity<Map<String, Object>> simulateUsage(@PathVariable Long id) {
        boolean success = dispatchService.simulateUsage(id);
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "模拟成功" : "模拟失败"
        ));
    }

    /**
     * 获取调度统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = dispatchService.getDispatchStats();
        return ResponseEntity.ok(stats);
    }
}
