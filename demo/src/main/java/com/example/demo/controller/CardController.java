package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Card;
import com.example.demo.service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 支付卡控制器
 * 处理用户支付卡的增删改查操作
 *
 * 【安全设计】
 * - 路径: /api/users/me/cards/*
 * - 所有接口需要用户认证
 * - 绝不返回完整卡号和CVV
 *
 * 【PCI DSS 合规】
 * - 前端应使用第三方支付SDK（如Stripe）获取token
 * - 后端只存储脱敏后的卡片信息
 */
@RestController
@RequestMapping("/api/users/me/cards")
@CrossOrigin
public class CardController {

    @Autowired
    private CardService cardService;

    /**
     * 获取当前用户的支付卡列表
     * GET /api/users/me/cards
     *
     * @return 脱敏后的卡片列表（只含后4位）
     */
    @GetMapping
    public Result<List<Card>> getMyCards(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Card> cards = cardService.findByUserId(userId);
        return Result.success(cards);
    }

    /**
     * 添加新支付卡
     * POST /api/users/me/cards
     *
     * 【安全处理】
     * - 完整卡号用于提取后4位和识别卡片类型
     * - CVV 验证后立即丢弃，不存储
     *
     * @param card 卡片信息（前端传入完整卡号）
     * @param request HTTP请求（获取用户ID）
     * @return 操作结果
     */
    @PostMapping
    public Result<Card> addCard(@RequestBody Card card, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        // 调用安全的服务方法，只存储脱敏信息
        Card savedCard = cardService.saveCard(
            card.getLastFour(),  // 前端传完整卡号，这里用lastFour做演示
            card.getCardHolder(),
            card.getExpiryDate(),
            card.getLastFour(),  // CVV参数（演示中不实际使用）
            userId,
            card.getIsDefault()
        );

        return Result.success(savedCard);
    }

    /**
     * 设置默认支付卡
     * PUT /api/users/me/cards/{id}/default
     *
     * @param id 卡片ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/{id}/default")
    public Result<String> setDefaultCard(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = cardService.setDefault(userId, id);
        if (success) {
            return Result.success("Default card updated");
        }
        return Result.error("Failed to update default card");
    }

    /**
     * 删除支付卡
     * DELETE /api/users/me/cards/{id}
     *
     * 只能删除属于当前用户的卡，防止越权删除
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCard(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Card card = cardService.findById(id);

        // 安全检查：确保卡片存在且属于当前用户
        if (card == null || !card.getUserId().equals(userId)) {
            return Result.error("Card not found");
        }

        if (cardService.deleteById(id)) {
            return Result.success("Card deleted successfully");
        }
        return Result.error("Failed to delete card");
    }
}
