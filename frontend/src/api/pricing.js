import request from '@/utils/request'

export const getPricingList = () =>
  request({ url: '/api/pricing', method: 'get' })
