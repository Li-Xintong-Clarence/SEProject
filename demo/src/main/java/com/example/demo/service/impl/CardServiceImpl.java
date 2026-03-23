package com.example.demo.service.impl;

import com.example.demo.entity.Card;
import com.example.demo.mapper.CardMapper;
import com.example.demo.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 支付卡服务实现类
 * 实现支付卡相关的具体业务逻辑
 * 支持设置默认卡、自动清除其他默认卡等功能
 */
@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardMapper cardMapper;

    @Override
    public List<Card> findByUserId(Long userId) {
        return cardMapper.findByUserId(userId);
    }

    @Override
    public Card findById(Long id) {
        return cardMapper.findById(id);
    }

    /**
     * 保存支付卡
     * 如果设置为默认卡，先清除用户其他默认卡
     */
    @Override
    public boolean save(Card card) {
        if (card.getIsDefault() != null && card.getIsDefault()) {
            cardMapper.clearDefaultByUserId(card.getUserId());
        }
        return cardMapper.insert(card) > 0;
    }

    /**
     * 更新支付卡
     * 如果设置为默认卡，先清除用户其他默认卡
     */
    @Override
    public boolean update(Card card) {
        if (card.getIsDefault() != null && card.getIsDefault()) {
            cardMapper.clearDefaultByUserId(card.getUserId());
        }
        return cardMapper.update(card) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return cardMapper.deleteById(id) > 0;
    }
}
