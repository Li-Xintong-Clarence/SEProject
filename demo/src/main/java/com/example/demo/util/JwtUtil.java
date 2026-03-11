package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 */
public class JwtUtil {
    private static final String SECRET_KEY = "scooter_rental_secret_key_for_jwt_token_generation_2024";
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    /**
     * 获取签名密钥
     * @return 用于JWT签名的密钥
     */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * @param username 用户名
     * @param role 用户角色
     * @return JWT令牌字符串
     */
    public static String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中提取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * 从令牌中提取角色
     * @param token JWT令牌
     * @return 用户角色
     */
    public static String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    public static boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析令牌中的Claims（负载）
     * @param token JWT令牌
     * @return Claims对象
     */
    private static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
