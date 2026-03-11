package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.HireOption;
import com.example.demo.service.HireOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 租用选项控制器
 * 处理租用选项相关的API请求（ID4：查看租用选项和费用）
 * 包括查询租用选项、修改租用选项费用等操作
 */
@RestController
@RequestMapping("/api/hire-options")
@CrossOrigin
public class HireOptionController {

    @Autowired
    private HireOptionService hireOptionService;

    /**
     * 获取所有可用的租用选项（ID4：查看租用选项和费用）
     * 返回所有激活状态的租用选项（如1小时、4小时、1天、1周）
     * @return 可用租用选项列表
     */
    @GetMapping
    public Result<List<HireOption>> list() {
        return Result.success(hireOptionService.findAllActive());
    }

    /**
     * 更新租用选项费用（ID16：管理员配置租用选项费用）
     * 管理员可以修改租用选项的价格、时长等信息
     * @param id 租用选项ID
     * @param option 新的租用选项信息
     * @return 更新成功返回成功信息，失败返回错误信息
     */
    @PutMapping("/{id}")
    public Result<String> updateOption(@PathVariable Long id, @RequestBody HireOption option) {
        option.setId(id);
        if (hireOptionService.update(option)) {
            return Result.success("Hire option updated successfully");
        }
        return Result.error("Failed to update hire option");
    }
}
