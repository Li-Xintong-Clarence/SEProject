package com.example.demo.service.impl;

import com.example.demo.entity.Pricing;
import com.example.demo.mapper.PricingMapper;
import com.example.demo.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 价格配置服务实现类
 * 实现租赁价格相关的具体业务逻辑
 */
@Service
public class PricingServiceImpl implements PricingService {

    @Autowired
    private PricingMapper pricingMapper;

    @Override
    public List<Pricing> findAll() {
        return pricingMapper.findAll();
    }

    @Override
    public Pricing findById(Long id) {
        return pricingMapper.findById(id);
    }

    @Override
    public boolean save(Pricing pricing) {
        return pricingMapper.insert(pricing) > 0;
    }

    @Override
    public boolean update(Pricing pricing) {
        return pricingMapper.update(pricing) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return pricingMapper.deleteById(id) > 0;
    }
}
