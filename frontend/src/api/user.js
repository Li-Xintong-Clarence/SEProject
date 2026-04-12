import request from '@/utils/request'

export const getCurrentUser = () =>
  request({ url: '/api/users/me', method: 'get' })

export const getUserStats = () =>
  request({ url: '/api/users/me/stats', method: 'get' })

/** 管理员：用户列表（用于代客预订选用户） */
export const listUsers = () =>
  request({ url: '/api/users', method: 'get' })
