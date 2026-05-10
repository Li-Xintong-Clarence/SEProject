import request from '@/utils/request'

export const getMyFeedbacks = () =>
  request({ url: '/api/feedback/my', method: 'get' })

export const createFeedback = (data) =>
  request({ url: '/api/feedback', method: 'post', data })
