import request from '@/utils/request'

export const getMyCards = () =>
  request({ url: '/api/users/me/cards', method: 'get' })

export const addCard = (data) =>
  request({ url: '/api/users/me/cards', method: 'post', data })

export const deleteCard = (id) =>
  request({ url: `/api/users/me/cards/${id}`, method: 'delete' })
