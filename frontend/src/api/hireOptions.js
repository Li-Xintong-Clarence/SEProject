import request from '@/utils/request'

export const listHireOptions = () =>
  request({ url: '/api/hire-options', method: 'get' })

/** ID16 管理员更新租用选项 */
export const updateHireOption = (id, data) =>
  request({ url: `/api/hire-options/${id}`, method: 'put', data })
