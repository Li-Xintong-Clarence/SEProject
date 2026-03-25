package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置JWT拦截器，拦截需要认证的API请求
 * 配置请求日志拦截器，记录所有请求的IP、方法、路径
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private RequestLogInterceptor requestLogInterceptor;

    /**
     * 添加拦截器配置
     * 1. 请求日志拦截器：拦截所有请求，用于监控
     * 2. JWT拦截器：拦截所有 /api/** 路径的请求
     *    排除无需认证的路径：/auth/**（登录注册）、用户列表、电动车列表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 日志拦截器 - 拦截所有请求，不排除任何路径
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**");

        // JWT拦截器 - 只拦截需要认证的API
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/auth/**", "/api/users", "/api/scooters", "/api/scooters/available", "/api/pricing");
    }
}
