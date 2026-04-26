// src/api/discount.js
import request from '@/utils/request'

// 验证折扣码
export const validateDiscountCode = (code) =>
  request({
    url: '/api/discounts/validate',
    method: 'post',
    data: { code }
  })

// 获取折扣码信息
export const getDiscountCodeInfo = (code) =>
  request({
    url: `/api/discounts/${code}`,
    method: 'get'
  })

// 应用折扣码获取价格预览
export const applyDiscountCode = (code, hireOption) =>
  request({
    url: '/api/discounts/apply',
    method: 'post',
    data: { code, hireOption }
  })

// 获取用户可用的折扣码列表
export const getMyDiscountCodes = () =>
  request({
    url: '/api/discounts/my-codes',
    method: 'get'
  })

// 管理员：创建折扣码
export const createDiscountCode = (data) =>
  request({
    url: '/api/discounts',
    method: 'post',
    data
  })

// 管理员：获取所有折扣码
export const listDiscountCodes = () =>
  request({
    url: '/api/discounts',
    method: 'get'
  })

// 管理员：更新折扣码状态
export const updateDiscountCodeStatus = (id, status) =>
  request({
    url: `/api/discounts/${id}/status`,
    method: 'put',
    data: { status }
  })
