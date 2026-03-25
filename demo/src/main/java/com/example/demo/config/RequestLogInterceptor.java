package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 请求日志拦截器
 * 记录每个HTTP请求的来源IP、方法、路径、参数等信息
 * 不拦截任何请求，仅用于调试和监控
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        log.info("[Request] IP: {} | {} {} | Query: {}",
                ip, method, uri, queryString == null ? "none" : queryString);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("[Response] {} {} | Status: {}",
                request.getMethod(), request.getRequestURI(), response.getStatus());
    }

    /**
     * 获取客户端真实IP地址
     * 兼容通过代理/Nginx转发的请求
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
