package com.example.demo.service;

import com.example.demo.entity.Card;
import java.util.List;

/**
 * 支付卡服务接口
 * 定义支付卡相关的业务操作
 */
public interface CardService {
    /**
     * 查询用户的支付卡列表
     */
    List<Card> findByUserId(Long userId);
    /**
     * 根据ID查询支付卡
     */
    Card findById(Long id);
    /**
     * 保存支付卡（新增）
     */
    boolean save(Card card);
    /**
     * 更新支付卡信息
     */
    boolean update(Card card);
    /**
     * 删除支付卡
     */
    boolean deleteById(Long id);
}
