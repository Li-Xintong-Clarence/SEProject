import request from '@/utils/request'

export const getPricingList = () =>
  request({ url: '/api/pricing', method: 'get' })

export const getPricePreview = (params) =>
  request({ url: '/api/pricing/preview', method: 'get', params })
