// src/api/scooter.js
import request from '@/utils/request'

export const getScooters = () => {
    return request({
        url: '/api/scooters',
        method: 'get'
    })
}

export const getAvailableScooters = () => {
    return request({
        url: '/api/scooters/available',
        method: 'get'
    })
}

export const getScooterById = (id) => {
    return request({
        url: `/api/scooters/${id}`,
        method: 'get'
    })
}

export const updateScooterStatus = (id, status) => {
    return request({
        url: `/api/scooters/${id}/status`,
        method: 'put',
        params: { status }
    })
}
