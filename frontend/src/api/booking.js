// src/api/booking.js
// 创建预订
import request from '@/utils/request'

export const createBooking = (data) => {
    return request({
        url: '/api/bookings',
        method: 'post',
        data
    })
}

export const getUserBookings = () => {
    return request({
        url: '/api/bookings',
        method: 'get'
    })
}

export const cancelBooking = (id) => {
    return request({
        url: `/api/bookings/${id}/cancel`,
        method: 'post'
    })
}

export const payBooking = (id, paymentData) => {
    return request({
        url: `/api/bookings/${id}/pay`,
        method: 'post',
        data: paymentData
    })
}

export const extendBooking = (id, hireOption) => {
    return request({
        url: `/api/bookings/${id}/extend`,
        method: 'put',
        params: { hireOption }
    })
}

export const getBookingConfirmation = (id) => {
    return request({
        url: `/api/bookings/${id}/confirmation`,
        method: 'get'
    })
}