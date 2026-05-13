package com.example.demo.service.impl;

import com.example.demo.entity.Card;
import com.example.demo.mapper.CardMapper;
import com.example.demo.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 支付卡服务实现类
 *
 * 【安全设计说明】
 * - 不存储完整卡号，只存储后4位用于识别
 * - 不存储 CVV
 * - 卡片类型自动识别（VISA、Mastercard等）
 *
 * @see Card
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
     *
     * 【安全处理】
     * 1. 提取并存储卡号后4位
     * 2. 自动识别卡片类型
     * 3. 不存储完整卡号和CVV
     *
     * @param cardNumber 完整卡号（来自前端，用于提取后4位）
     * @param cardHolder 持卡人姓名
     * @param expiryDate 有效期
     * @param cvv CVV码（验证后不存储）
     * @param userId 用户ID
     * @param isDefault 是否默认卡
     * @return 保存的卡片对象
     */
    @Override
    @Transactional
    public Card saveCard(String cardNumber, String cardHolder, String expiryDate,
                         String cvv, Long userId, Boolean isDefault) {

        // 安全处理：只存储后4位
        String lastFour = null;
        if (cardNumber != null && cardNumber.length() >= 4) {
            lastFour = cardNumber.substring(cardNumber.length() - 4);
        }

        // 识别卡片类型
        String cardType = detectCardType(cardNumber);

        Card card = new Card();
        card.setUserId(userId);
        card.setCardHolder(cardHolder);
        card.setLastFour(lastFour);
        card.setCardType(cardType);
        card.setExpiryDate(expiryDate);
        card.setIsDefault(isDefault);

        // 如果设置为默认卡，先清除其他默认卡
        if (Boolean.TRUE.equals(isDefault)) {
            cardMapper.clearDefaultByUserId(userId);
        }

        cardMapper.insert(card);
        return card;
    }

    /**
     * 识别卡片类型
     * 基于卡号前缀（BIN/IIN）识别发卡机构
     *
     * @param cardNumber 卡号
     * @return 卡片类型字符串
     */
    private String detectCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "UNKNOWN";
        }

        // 移除空格和连字符
        String cleanNumber = cardNumber.replaceAll("[\\s-]", "");

        if (cleanNumber.matches("^4\\d*")) {
            return "VISA";
        } else if (cleanNumber.matches("^5[1-5]\\d*")) {
            return "Mastercard";
        } else if (cleanNumber.matches("^3[47]\\d*")) {
            return "American Express";
        } else if (cleanNumber.matches("^6(?:011|5\\d)\\d*")) {
            return "Discover";
        } else if (cleanNumber.matches("^62\\d*")) {
            return "UnionPay";
        }

        return "UNKNOWN";
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return cardMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean setDefault(Long userId, Long cardId) {
        cardMapper.clearDefaultByUserId(userId);
        Card card = cardMapper.findById(cardId);
        if (card != null && card.getUserId().equals(userId)) {
            card.setIsDefault(true);
            return cardMapper.update(card) > 0;
        }
        return false;
    }
}
