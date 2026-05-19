import request from '@/utils/request'

export const getPricingList = () =>
  request({ url: '/api/pricing', method: 'get' })

export const getPricePreview = (params) =>
  request({ url: '/api/pricing/preview', method: 'get', params })

export const addPricing = (data) =>
  request({ url: '/api/pricing', method: 'post', data })

export const updatePricing = (data) =>
  request({ url: '/api/pricing', method: 'put', data })

export const deletePricing = (id) =>
  request({ url: `/api/pricing/${id}`, method: 'delete' })
