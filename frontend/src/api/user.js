import request from '@/utils/request'

export const getCurrentUser = () =>
  request({ url: '/api/users/me', method: 'get' })

export const getUserStats = () =>
  request({ url: '/api/users/me/stats', method: 'get' })

export const listUsers = () =>
  request({ url: '/api/users', method: 'get' })
