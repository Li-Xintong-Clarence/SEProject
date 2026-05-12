import request from '@/utils/request'

export function getDepots() {
  return request({
    url: '/api/depots',
    method: 'get'
  })
}

export function getActiveDepots() {
  return request({
    url: '/api/depots/active',
    method: 'get'
  })
}

export function getAvailableDepots() {
  return request({
    url: '/api/depots/available',
    method: 'get'
  })
}

export function getDepotById(id) {
  return request({
    url: `/api/depots/${id}`,
    method: 'get'
  })
}
