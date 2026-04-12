// src/api/scooter.js
import request from '@/utils/request'

// mock 数据（等后端调通后注释掉这部分）
const mockScooters = [
    { id: 1, name: '滑板车 A', status: 'available', pricePerHour: 5, location: 'A区', lat: 31.23, lng: 121.47 },
    { id: 2, name: '滑板车 B', status: 'available', pricePerHour: 5, location: 'B区', lat: 31.24, lng: 121.48 },
    { id: 3, name: '滑板车 C', status: 'unavailable', pricePerHour: 5, location: 'C区', lat: 31.25, lng: 121.49 },
    { id: 4, name: '滑板车 D', status: 'available', pricePerHour: 5, location: 'D区', lat: 31.26, lng: 121.50 },
    { id: 5, name: '滑板车 E', status: 'available', pricePerHour: 5, location: 'E区', lat: 31.27, lng: 121.51 }
]

// 获取所有滑板车
export const getScooters = () => {
    // 真实接口（等后端调通后取消注释）
    return request({
        url: '/api/scooters',
        method: 'get'
    })
}

// 获取可用滑板车
export const getAvailableScooters = () => {
    return request({
        url: '/api/scooters/available',
        method: 'get'
    })
}

// 获取单个滑板车详情
export const getScooterById = (id) => {
    return request({
        url: `/api/scooters/${id}`,
        method: 'get'
    })
}

/** ID10 更新车辆状态（管理员/员工） */
export const updateScooterStatus = (id, status) => {
    return request({
        url: `/api/scooters/${id}/status`,
        method: 'put',
        params: { status }
    })
}