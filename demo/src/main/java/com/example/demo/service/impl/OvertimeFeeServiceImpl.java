package com.example.demo.service.impl;

import com.example.demo.entity.OvertimeFee;
import com.example.demo.mapper.OvertimeFeeMapper;
import com.example.demo.service.OvertimeFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 超时费用配置服务实现类
 */
@Service
public class OvertimeFeeServiceImpl implements OvertimeFeeService {

    @Autowired
    private OvertimeFeeMapper overtimeFeeMapper;

    @Override
    public List<OvertimeFee> findAll() {
        return overtimeFeeMapper.findAll();
    }

    @Override
    public OvertimeFee findById(Long id) {
        return overtimeFeeMapper.findById(id);
    }

    @Override
    public OvertimeFee findByHireOption(String hireOption) {
        return overtimeFeeMapper.findByHireOption(hireOption);
    }

    @Override
    public boolean save(OvertimeFee overtimeFee) {
        // 检查是否已存在
        if (overtimeFeeMapper.countByHireOption(overtimeFee.getHireOption()) > 0) {
            return false;
        }
        return overtimeFeeMapper.insert(overtimeFee) > 0;
    }

    @Override
    public boolean update(OvertimeFee overtimeFee) {
        return overtimeFeeMapper.update(overtimeFee) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return overtimeFeeMapper.deleteById(id) > 0;
    }
}
