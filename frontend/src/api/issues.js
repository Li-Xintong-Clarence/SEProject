import request from '@/utils/request'

/** ID13 车辆/故障短反馈（IssueReport） */
export const createIssueReport = (data) =>
  request({ url: '/api/issues', method: 'post', data })

export const getMyIssueReports = () =>
  request({ url: '/api/issues/my', method: 'get' })
