package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 折扣控制器
 * 处理折扣码验证和应用
 */
@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "*")
public class DiscountController {

    /**
     * 优惠码数据（内存存储，实际生产应使用数据库）
     */
    private static final Map<String, DiscountCode> DISCOUNT_CODES = new HashMap<>();

    static {
        DISCOUNT_CODES.put("WELCOME10", new DiscountCode("WELCOME10", 10, "欢迎新用户立减10%", true));
        DISCOUNT_CODES.put("SUMMER20", new DiscountCode("SUMMER20", 20, "夏日特惠8折", true));
        DISCOUNT_CODES.put("STUDENT15", new DiscountCode("STUDENT15", 15, "学生专享85折", true));
        DISCOUNT_CODES.put("VIP30", new DiscountCode("VIP30", 30, "VIP会员7折优惠", true));
    }

    /**
     * 获取所有折扣码列表
     * GET /api/discounts
     */
    @GetMapping
    public ResponseEntity<?> listDiscounts() {
        List<Map<String, Object>> result = new ArrayList<>();
        DISCOUNT_CODES.forEach((code, discount) -> {
            result.add(new HashMap<String, Object>() {{
                put("code", discount.code);
                put("discountPercent", discount.discountPercent);
                put("description", discount.description);
                put("active", discount.active);
            }});
        });
        return ResponseEntity.ok(result);
    }

    /**
     * 验证折扣码
     * GET /api/discounts/validate/{code}
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validateDiscountCode(@PathVariable String code) {
        DiscountCode discount = DISCOUNT_CODES.get(code.toUpperCase());
        if (discount == null || !discount.active) {
            return ResponseEntity.ok(Collections.singletonMap("valid", false));
        }
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("valid", true);
            put("discountPercent", discount.discountPercent);
            put("description", discount.description);
            put("code", discount.code);
        }});
    }

    /**
     * 获取折扣码信息
     * GET /api/discounts/{code}
     */
    @GetMapping("/{code}")
    public ResponseEntity<?> getDiscountInfo(@PathVariable String code) {
        DiscountCode discount = DISCOUNT_CODES.get(code.toUpperCase());
        if (discount == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discount);
    }

    /**
     * 应用折扣码到预订
     * POST /api/discounts/apply
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyDiscountCode(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        Long bookingId = request.get("bookingId") != null ?
            Long.valueOf(request.get("bookingId").toString()) : null;

        DiscountCode discount = DISCOUNT_CODES.get(code.toUpperCase());
        if (discount == null || !discount.active) {
            return ResponseEntity.ok(Collections.singletonMap("success", false));
        }

        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("success", true);
            put("code", discount.code);
            put("discountPercent", discount.discountPercent);
            put("bookingId", bookingId);
        }});
    }

    /**
     * 折扣码内部类
     */
    private static class DiscountCode {
        String code;
        int discountPercent;
        String description;
        boolean active;

        DiscountCode(String code, int discountPercent, String description, boolean active) {
            this.code = code;
            this.discountPercent = discountPercent;
            this.description = description;
            this.active = active;
        }
    }
}
