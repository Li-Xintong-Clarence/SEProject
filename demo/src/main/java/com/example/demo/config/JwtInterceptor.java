package com.example.demo.config;

import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 在请求到达Controller之前验证Token
 * 将解析出的userId、username、role存入request供后续使用
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    /**
     * 验证JWT Token
     * 1. 检查请求头是否包含Authorization
     * 2. 验证Token格式和有效性
     * 3. 解析Token并将用户信息存入request
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"Missing or invalid token\"}");
            return false;
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"Token expired or invalid\"}");
            return false;
        }

        request.setAttribute("userId", JwtUtil.getUserId(token));
        request.setAttribute("username", JwtUtil.getUsername(token));
        request.setAttribute("role", JwtUtil.getRole(token));
        return true;
    }
}
