import request from '@/utils/request'

export const createIssueReport = (data) =>
  request({ url: '/api/issues', method: 'post', data })

export const getMyIssueReports = () =>
  request({ url: '/api/issues/my', method: 'get' })
