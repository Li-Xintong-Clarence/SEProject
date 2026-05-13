import request from '@/utils/request'

export const listDiscounts = () =>
  request({
    url: '/api/discounts',
    method: 'get'
  })

export const applyDiscountCode = (code, bookingId) =>
  request({
    url: '/api/discounts/apply',
    method: 'post',
    data: { code, bookingId }
  })

export const validateDiscountCode = (code) =>
  request({
    url: `/api/discounts/validate/${code}`,
    method: 'get'
  })

export const getDiscountInfo = (code) =>
  request({
    url: `/api/discounts/${code}`,
    method: 'get'
  })
