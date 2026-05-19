package com.example.demo.service;

import com.example.demo.entity.OvertimeFee;
import java.util.List;

/**
 * 超时费用配置服务接口
 */
public interface OvertimeFeeService {
    List<OvertimeFee> findAll();
    OvertimeFee findById(Long id);
    OvertimeFee findByHireOption(String hireOption);
    boolean save(OvertimeFee overtimeFee);
    boolean update(OvertimeFee overtimeFee);
    boolean deleteById(Long id);
}
