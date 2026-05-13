package com.example.demo.service;

import com.example.demo.entity.Card;
import java.util.List;

/**
 * 支付卡服务接口
 *
 * 【安全说明】
 * - 所有方法只处理脱敏后的卡片信息
 * - 完整卡号和CVV不存储
 *
 * @see com.example.demo.entity.Card
 */
public interface CardService {

    /**
     * 查询用户的支付卡列表
     * @param userId 用户ID
     * @return 卡片列表（已脱敏）
     */
    List<Card> findByUserId(Long userId);

    /**
     * 根据ID查询支付卡
     * @param id 卡片ID
     * @return 卡片对象（已脱敏）
     */
    Card findById(Long id);

    /**
     * 保存支付卡（新增）
     *
     * 【安全处理】
     * - 只存储卡号后4位
     * - 自动识别卡片类型
     * - 不存储完整卡号和CVV
     *
     * @param cardNumber 完整卡号（用于提取后4位和识别类型）
     * @param cardHolder 持卡人姓名
     * @param expiryDate 有效期
     * @param cvv CVV码（验证后丢弃）
     * @param userId 用户ID
     * @param isDefault 是否默认卡
     * @return 保存的卡片对象
     */
    Card saveCard(String cardNumber, String cardHolder, String expiryDate,
                  String cvv, Long userId, Boolean isDefault);

    /**
     * 删除支付卡
     * @param id 卡片ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 设置默认支付卡
     *
     * @param userId 用户ID
     * @param cardId 卡片ID
     * @return 是否设置成功
     */
    boolean setDefault(Long userId, Long cardId);
}
