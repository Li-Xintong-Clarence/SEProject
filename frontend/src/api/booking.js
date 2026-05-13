// src/api/booking.js
import request from '@/utils/request'

export const createBookingByDepot = (depotId, hireOption) => {
    return request({
        url: '/api/bookings/depot',
        method: 'post',
        data: { depotId, hireOption }
    })
}

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

export const returnScooter = (id, endDepotId) => {
    return request({
        url: `/api/bookings/${id}/return`,
        method: 'post',
        data: { endDepotId }
    })
}

export const getMyActiveBookings = () => {
    return request({
        url: '/api/bookings/current',
        method: 'get'
    })
}

export const getBookingById = (id) => {
    return request({
        url: `/api/bookings/${id}`,
        method: 'get'
    })
}

export const endBooking = (id) => {
    return request({
        url: `/api/bookings/${id}/end`,
        method: 'post'
    })
}
