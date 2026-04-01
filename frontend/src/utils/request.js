// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE || 'http://47.108.188.221:8081', // 从环境变量读取，方便切换
    timeout: 10000 // 10秒超时
})

// 请求拦截器：在请求头中添加 token（如果存在）
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        console.log('请求拦截器执行，token:', token)   // 新增
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器：统一处理错误提示
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res.data  // 直接返回 data 部分
    },
    error => {
        // 网络错误或超时
        let message = '网络错误，请稍后重试'
        if (error.response) {
            // 根据后端返回的错误状态码自定义提示
            switch (error.response.status) {
                case 401:
                    message = '未授权，请重新登录'
                    // 可以在这里跳转到登录页
                    break
                case 403:
                    message = '拒绝访问'
                    break
                case 404:
                    message = '请求地址不存在'
                    break
                case 500:
                    message = '服务器内部错误'
                    break
                default:
                    message = error.response.data?.message || '请求失败'
            }
        }
        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default request