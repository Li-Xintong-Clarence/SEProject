package com.example.demo.service.impl;

import com.example.demo.entity.HireOption;
import com.example.demo.mapper.HireOptionMapper;
import com.example.demo.service.HireOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 租用选项服务实现类
 * 实现租用选项相关的业务逻辑（ID4：查看租用选项和费用）
 */
@Service
public class HireOptionServiceImpl implements HireOptionService {

    @Autowired
    private HireOptionMapper hireOptionMapper;

    /**
     * 获取所有可用的租用选项
     */
    @Override
    public List<HireOption> findAllActive() {
        return hireOptionMapper.findAllActive();
    }

    /**
     * 更新租用选项
     */
    @Override
    public boolean update(HireOption option) {
        return hireOptionMapper.update(option) > 0;
    }
}
