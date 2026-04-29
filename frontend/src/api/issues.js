import request from '@/utils/request'

export const createIssueReport = (data) =>
  request({ url: '/api/issues', method: 'post', data })

export const getMyIssueReports = () =>
  request({ url: '/api/issues/my', method: 'get' })

export const getAllIssueReports = () =>
  request({ url: '/api/issues', method: 'get' })

export const updateIssueReport = (id, data) =>
  request({ url: `/api/issues/${id}`, method: 'put', data })
