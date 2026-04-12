import request from '@/utils/request'

export const adminCreateBooking = (data) =>
  request({ url: '/api/admin/bookings', method: 'post', data })

export const getWeeklyIncomeReport = () =>
  request({ url: '/api/admin/reports/income/weekly', method: 'get' })

export const getDailyIncomeReport = () =>
  request({ url: '/api/admin/reports/income/daily', method: 'get' })

export const getAllFeedbacks = (params = {}) =>
  request({ url: '/api/admin/feedback', method: 'get', params })

export const getHighPriorityIssues = () =>
  request({ url: '/api/admin/issues', method: 'get' })

export const processFeedback = (id, data) =>
  request({ url: `/api/admin/feedback/${id}`, method: 'put', data })

export const getAdminPricing = () =>
  request({ url: '/api/admin/pricing', method: 'get' })

export const updateAdminPricing = (data) =>
  request({ url: '/api/admin/pricing', method: 'put', data })
