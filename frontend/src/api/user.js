import request from '@/utils/request'

export const getCurrentUser = () =>
  request({ url: '/api/users/me', method: 'get' })

export const getUserStats = (userId = null) =>
  request({ url: userId ? `/api/users/${userId}/stats` : '/api/users/me/stats', method: 'get' })

export const listUsers = () =>
  request({ url: '/api/users', method: 'get' })

export const getAllUsers = () =>
  request({ url: '/api/users/all', method: 'get' })

export const updateUserStatus = (id, status) =>
  request({ url: `/api/users/${id}/status`, method: 'put', data: { status } })
