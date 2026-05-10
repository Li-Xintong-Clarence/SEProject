// src/api/auth.js
import request from '@/utils/request'

// 登录
export function login(data) {
    return request({
        url: '/auth/login',
        method: 'post',
        data
    })
}

// 注册（真实接口）
export function register(data) {
    return request({
        url: '/auth/register',
        method: 'post',
        data
    })
}