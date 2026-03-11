package com.example.demo.service;

import com.example.demo.entity.HireOption;
import java.util.List;

/**
 * 租用选项服务接口
 * 定义租用选项相关的业务逻辑方法（ID4：查看租用选项和费用）
 */
public interface HireOptionService {
    /**
     * 获取所有可用的租用选项
     * @return 可用租用选项列表
     */
    List<HireOption> findAllActive();

    /**
     * 更新租用选项（管理员功能）
     * @param option 租用选项对象
     * @return 是否更新成功
     */
    boolean update(HireOption option);
}
