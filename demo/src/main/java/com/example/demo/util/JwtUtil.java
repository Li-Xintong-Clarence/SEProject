package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT令牌
 * Token有效期：24小时
 */
public class JwtUtil {
    /** 密钥（至少256位） */
    private static final String SECRET_KEY = "scooter-rental-secret-key-must-be-at-least-256-bits";
    /** Token有效期：24小时（毫秒） */
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /** 使用HMAC-SHA算法创建密钥 */
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT Token字符串
     */
    public static String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * 解析Token，获取Claims（包含所有声明）
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从Token中获取用户ID
     */
    public static Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从Token中获取用户角色
     */
    public static String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * 检查Token是否过期
     * @return true表示已过期，false表示有效
     */
    public static boolean isTokenExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }

    /**
     * 验证Token是否有效
     * @return true表示有效，false表示无效（过期或格式错误）
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
