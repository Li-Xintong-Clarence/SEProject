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
 * 路径: /api/users/me/cards/*
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
     */
    @GetMapping
    public Result<List<Card>> getMyCards(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Card> cards = cardService.findByUserId(userId);
        // 不返回 CVV（安全考虑）
        for (Card card : cards) {
            card.setCvv(null);
        }
        return Result.success(cards);
    }

    /**
     * 添加新支付卡
     * POST /api/users/me/cards
     * 参数: cardNumber, cardHolder, expiryDate, cvv 等
     */
    @PostMapping
    public Result<String> addCard(@RequestBody Card card, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        card.setUserId(userId);
        if (cardService.save(card)) {
            return Result.success("Card added successfully");
        }
        return Result.error("Failed to add card");
    }

    /**
     * 删除支付卡
     * DELETE /api/users/me/cards/{id}
     * 只能删除属于当前用户的卡
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCard(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Card card = cardService.findById(id);
        if (card == null || !card.getUserId().equals(userId)) {
            return Result.error("Card not found");
        }
        if (cardService.deleteById(id)) {
            return Result.success("Card deleted successfully");
        }
        return Result.error("Failed to delete card");
    }
}
