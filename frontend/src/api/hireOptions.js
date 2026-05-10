import request from '@/utils/request'

export const listHireOptions = () =>
  request({ url: '/api/hire-options', method: 'get' })

export const updateHireOption = (id, data) =>
  request({ url: `/api/hire-options/${id}`, method: 'put', data })
