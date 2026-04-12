import request from '@/utils/request'

/** 公开价格列表（与 booking 计价一致） */
export const getPricingList = () =>
  request({ url: '/api/pricing', method: 'get' })
