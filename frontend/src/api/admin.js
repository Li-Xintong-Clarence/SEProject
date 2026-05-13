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
  request({ url: `/api/feedback/${id}`, method: 'put', data })

export const getAdminPricing = () =>
  request({ url: '/api/admin/pricing', method: 'get' })

export const updateAdminPricing = (data) =>
  request({ url: '/api/admin/pricing', method: 'put', data })

// ============ 统计 API ============

export const getStatisticsOverview = () =>
  request({ url: '/api/statistics/overview', method: 'get' })

export const getStatisticsUsers = () =>
  request({ url: '/api/statistics/users', method: 'get' })

export const getStatisticsBookings = () =>
  request({ url: '/api/statistics/bookings', method: 'get' })

export const getStatisticsScooters = () =>
  request({ url: '/api/statistics/scooters', method: 'get' })

export const getStatisticsUserGrowth = () =>
  request({ url: '/api/statistics/users/growth', method: 'get' })

export const getStatisticsTopUsers = () =>
  request({ url: '/api/statistics/users/top', method: 'get' })

export const getStatisticsBookingStatus = () =>
  request({ url: '/api/statistics/bookings/status', method: 'get' })

export const getStatisticsPeakHours = () =>
  request({ url: '/api/statistics/bookings/peak-hours', method: 'get' })
